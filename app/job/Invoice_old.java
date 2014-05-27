package job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

import play.db.DB;
import play.jobs.Job;
import play.jobs.On;

//@On("0 40 23 L * ?")
public class Invoice_old extends Job {
	
	public void doJob() {
		Connection con=DB.getConnection();	//0 40 23 L * ?	 El ultimo dia de cada mes a las 23:40
    	try {    		
    		int resultado = 0;
    		StringBuilder query = new StringBuilder();
    		query.append("INSERT INTO MGDB_INVOICE (CO_INVOICE, CO_MEMBER, CO_PROVIDER, AMOUNT, DA_DATE_CLOSE, ST_INVOICE, CO_USER_CREATE, DA_DATE_CREATE) ");
       	    query.append("SELECT SEQ_INVOICE.NEXTVAL, ME.CO_MEMBER, ME.CO_PROVIDER, NVL(PR.AMOUNT,0) AS AMOUNT, SYSDATE, '1', 'DEMON', SYSDATE ");
       	    query.append("FROM MGDB_MEMBER ME, MGDB_PROVIDER PR ");
       	    query.append("WHERE ME.CO_PROVIDER = PR.CO_PROVIDER AND ME.ST_MEMBER = '1' ");   	       		
 	    	resultado = con.prepareStatement(query.toString()).executeUpdate(); 	    	
    		if(resultado>=1){    			
    			con.commit();
    			System.out.println("insert_invoices_OK");
    		}else if(resultado==0){    			
    			con.rollback();    
    			System.out.println("insert_invoises_ERROR");
    		}
    		con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }	
	
	
  
}
