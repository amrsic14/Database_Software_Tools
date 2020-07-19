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
import rs.etf.sab.operations.StockroomOperations;

/**
 *
 * @author acamr
 */
public class ma160414_StockroomOperations implements StockroomOperations {

    public ma160414_StockroomOperations() {
    }

    @Override
    public int deleteStockroomFromCity(int i) {
        CallableStatement cs = DB.call("spDeleteLoakacijaMagacinaIzGrada",
                i, DB.outParams.Integer);
        try {
            return cs.getInt(2);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
                
    }

    @Override
    public List<Integer> getAllStockrooms() {
        List<Integer> list = new ArrayList<>();
        List<List<String>> rs = DB.select("LokacijaMagacina",
                new String[]{"Adresa"},
                null,
                null,
                null);
        
        rs.forEach((l) -> {
            list.add(Integer.parseInt(l.get(0)));
        });
        
        return list;
    }

    @Override
    public int insertStockroom(int i) {
        CallableStatement cs = DB.call("spInsertLokacijaMagacina",
                i, DB.outParams.Integer);
        try {
            return cs.getInt(2);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    @Override
    public boolean deleteStockroom(int i){ 
        CallableStatement cs = DB.call("spDeleteLoakacijaMagacina",
                i, DB.outParams.Boolean);
        try {
            return cs.getBoolean(2);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
}
