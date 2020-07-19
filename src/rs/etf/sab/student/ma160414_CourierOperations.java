/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author acamr
 */
public class ma160414_CourierOperations implements CourierOperations {

    public ma160414_CourierOperations() {
    }

    @Override
    public boolean insertCourier(String string, String string1) {
        CallableStatement cs = DB.call("spInsertKurir",
                string, string1, DB.outParams.Boolean);
        
        try {
            return cs.getBoolean(3);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean deleteCourier(String string) {
        int ret = DB.deleteAND("Kurir",
                new String[]{"KorisnickoIme", "Status"},
                new String[]{string, "1"});
        
        return 1 == ret;
    }

    @Override
    public List<String> getCouriersWithStatus(int i) {
        List<List<String>> list = DB.select("Kurir",
                new String[]{"KorisnickoIme"},
                new String[]{"Status"},
                new String[]{String.valueOf(i)},
                null);
        
        List<String> idStr = new ArrayList<>();
        
        list.forEach((s) -> {
            idStr.add(s.get(0));
        });
        
        return idStr;
    }

    @Override
    public List<String> getAllCouriers() {
        List<List<String>> list = DB.select("Kurir",
                new String[]{"KorisnickoIme"},
                null,
                null,
                "ORDER BY Profit DESC");
        
        List<String> idStr = new ArrayList<>();
        
        list.forEach((s) -> {
            idStr.add(s.get(0));
        });
        
        return idStr;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int i) {
        CallableStatement cs = DB.call("spProsecanProfitKurir",
                i, DB.outParams.Double);
        
        try {
            BigDecimal ret = new BigDecimal(cs.getDouble(2));
            return ret;
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new BigDecimal(0);
        }
    }
    
}
