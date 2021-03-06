package com.yxzhm.motguest;

import com.yxzhm.hibernate.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by mot on 4/1/16.
 */
@Path("/MotGuest")
public class MotGuest {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getWifiPsw(@QueryParam("encry") boolean encry) throws UnsupportedEncodingException {
        String result="";
        Session session = HibernateUtil.getSession();
        Criteria c = session.createCriteria(WifiEntity.class);
        c.addOrder(Order.desc("id"));
        c.setMaxResults(1);
        List<WifiEntity> queryResult = c.list();
        if(queryResult.size()>0){
            result= queryResult.get(0).getPassword();
        }
        if(encry)
        {
            result=EncryString(result);
        }
        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String uploadWifiPsw(@FormParam("date") String date, @FormParam("pwd") String pwd,@FormParam("encry") boolean encry) throws UnsupportedEncodingException {

        if(encry) {
            date = DeEncryString(date);
            pwd = DeEncryString(pwd);
        }
        WifiEntity entity = new WifiEntity();

        entity.setWifidates(date);
        entity.setPassword(pwd);

        try {
            Session session = HibernateUtil.getSession();
            Criteria c = session.createCriteria(WifiEntity.class);
            c.add(Restrictions.eq("wifidates", date));
            c.add(Restrictions.eq("password", pwd));

            List<WifiEntity> queryResult = c.list();
            if (queryResult.size() > 0) {
                return "Existed";
            }

            Transaction t = session.beginTransaction();
            session.save(entity);
            t.commit();
        }
        catch (Exception e){
            return e.getMessage();
        }
        return "Success";

    }
    private static String EncryString(String s ) throws UnsupportedEncodingException {
        byte[] data= s.getBytes("ASCII");
        for (int i=0;i<data.length;i++) {
            data[i]=(byte)(data[i]+1);
        }
        return new String(data);
    }
    private static String DeEncryString(String s) throws UnsupportedEncodingException {
        byte[] data= s.getBytes("ASCII");
        for (int i=0;i<data.length;i++) {
            data[i]=(byte)(data[i]-1);
        }
        return new String(data);
    }
}
