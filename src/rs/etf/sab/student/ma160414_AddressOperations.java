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
import rs.etf.sab.operations.AddressOperations;

/**
 *
 * @author acamr
 */
public class ma160414_AddressOperations implements AddressOperations {
    
    public ma160414_AddressOperations() {
    }
    
    @Override
    public int deleteAllAddressesFromCity(int i) {
        return DB.deleteAND("Adresa",
                new String[]{"Grad"},
                new String[]{String.valueOf(i)});
    }
    
    @Override
    public int insertAddress(String string, int i, int i1, int i2, int i3) {
        CallableStatement cs = DB.call(
                "spInsertAdresa", 
                string, String.valueOf(i), i1, i2, i3, DB.outParams.Integer, DB.outParams.String);
        try {
//            System.out.println(cs.getString(7));
            return cs.getInt(6);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    @Override
    public int deleteAddresses(String string, int i) {
        return DB.deleteAND("Adresa",
                new String[]{"Ulica", "Broj"},
                new String[]{string ,String.valueOf(i)});
    }

    @Override
    public boolean deleteAdress(int i) {
        return 1 == DB.deleteAND("Adresa",
                new String[]{"ID"},
                new String[]{String.valueOf(i)});
    }

    @Override
    public List<Integer> getAllAddresses() {
        List<Integer> list = new ArrayList<>();
        List<List<String>> rs = DB.select("Adresa",
                new String[]{"ID"},
                null,
                null,
                null);
        
        rs.forEach((l) -> {
            list.add(Integer.parseInt(l.get(0)));
        });
        
        return list;
    }

    @Override
    public List<Integer> getAllAddressesFromCity(int i) {
        List<Integer> list = new ArrayList<>();
        List<List<String>> rs = DB.select("Adresa",
                new String[]{"ID"},
                new String[]{"Grad"},
                new String[]{String.valueOf(i)},
                null);
        
        rs.forEach((l) -> {
            list.add(Integer.parseInt(l.get(0)));
        });
        
        return list.size() > 0 ? list : null ;
    }
    
}