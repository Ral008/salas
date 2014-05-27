package job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

import play.db.DB;
import play.jobs.Job;
import play.jobs.On;

//@On("0 0 0 1 * ?")
public class UpdateStatus_old extends Job {
	
	public void doJob() {
		Connection con=DB.getConnection();	//0 50 4 13 * ?	 El 13 de cada mes a las 4:50
    	try {    		
    		int resultado = 0;
    		String sql="UPDATE MGDB_MEMBER SET CO_USER_MODIFY = ?, DA_DATE_MODIFY = SYSDATE , ST_MEMBER = ? WHERE ST_MEMBER = ? AND TO_CHAR(EFFECTIVE_DATE,'DD/MM/YYYY') <= TO_CHAR(SYSDATE,'DD/MM/YYYY') ";
    		PreparedStatement pstm = con.prepareStatement(sql);
    		pstm.setString(1, "DEMON");
    		pstm.setString(2, "1");
    		pstm.setString(3, "2");
    		resultado=pstm.executeUpdate();
    		if(resultado>=1){    			
    			con.commit();
    			System.out.println("update_status_OK");
    		}else if(resultado==0){    			
    			con.rollback();    
    			System.out.println("update_status_ERROR");
    		}
    		con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }	
	
	
  
}
