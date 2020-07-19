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
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author acamr
 */
public class ma160414_VehicleOperations implements VehicleOperations {

    public ma160414_VehicleOperations() {
    }

    @Override
    public boolean insertVehicle(String string, int i, BigDecimal bd, BigDecimal bd1) {
        CallableStatement cs = DB.call("spInsertVozilo",
                string, i, bd, bd1, DB.outParams.Boolean);
        
        try {
            return cs.getBoolean(5);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public int deleteVehicles(String... strings) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM [Vozilo] WHERE ");
        for(String username: strings){
            sb.append("RegistracioniBroj='").append(username).append("' OR ");
        }
        sb.setLength(sb.length() - 4);
        sb.append(";");
        
        return DB.delete(sb.toString());
    }

    @Override
    public List<String> getAllVehichles() {
        List<String> list = new ArrayList<>();
        List<List<String>> rs = DB.select("Vozilo",
                new String[]{"RegistracioniBroj"},
                null,
                null,
                null);
        
        rs.forEach((l) -> {
            list.add(l.get(0));
        });
        
        return list;
    }

    @Override
    public boolean changeFuelType(String string, int i) {
        CallableStatement cs = DB.call("spPromenaTipaGorivaVozilo",
                string, i, DB.outParams.Boolean);
        try {
            return cs.getBoolean(3);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean changeConsumption(String string, BigDecimal bd) {
        CallableStatement cs = DB.call("spPromenaPotrosnjeVozilo",
                string, bd, DB.outParams.Boolean);
        try {
            return cs.getBoolean(3);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean changeCapacity(String string, BigDecimal bd) {
        CallableStatement cs = DB.call("spPromenaNosivostiVozilo",
                string, bd, DB.outParams.Boolean);
        try {
            return cs.getBoolean(3);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean parkVehicle(String string, int i) {
        CallableStatement cs = DB.call("spParkiranjeVozila",
                string, i, DB.outParams.Boolean);
        try {
            return cs.getBoolean(3);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
}
