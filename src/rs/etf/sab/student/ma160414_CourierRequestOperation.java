/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author acamr
 */
public class ma160414_CourierRequestOperation implements CourierRequestOperation {

    public ma160414_CourierRequestOperation() {
    }

    @Override
    public boolean insertCourierRequest(String string, String string1) {
        CallableStatement cs = DB.call("spInsertZahtevKurir",
                string, string1, DB.outParams.Boolean);
        boolean css;
        try {
            css= cs.getBoolean(3);
            return css;
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean deleteCourierRequest(String string) {
        int ret = DB.deleteAND("ZahtevKurir",
                new String[]{"KorisnickoIme"},
                new String[]{string});
        
        return 1 == ret;
    }

    @Override
    public List<String> getAllCourierRequests() {
        List<String> list = new ArrayList<>();
        List<List<String>> rs = DB.select("ZahtevKurir",
                new String[]{"[KorisnickoIme]"},
                null,
                null,
                null);
        
        rs.forEach((l) -> {
            list.add(l.get(0));
        });
        
        return list;
    }

    @Override
    public boolean grantRequest(String string) {
        CallableStatement cs = DB.call("spPrihvatiZahtevKurir",
                string, DB.outParams.Boolean);
        
        try {
            return cs.getBoolean(2);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String korIme, String brDozvole) {
        return DB.update("ZahtevKurir",
                new String[]{"BrojDozvole"},
                new String[]{brDozvole},
                new String[]{"KorisnickoIme"},
                new String[]{korIme});
    }
    
}
