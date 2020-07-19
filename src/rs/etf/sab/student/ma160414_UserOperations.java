/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author acamr
 */
public class ma160414_UserOperations implements UserOperations {

    public ma160414_UserOperations() {
    }

    @Override
    public int deleteUsers(String... strings) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM [Korisnik] WHERE ");
        for(String username: strings){
            sb.append("KorisnickoIme='").append(username).append("' OR ");
        }
        sb.setLength(sb.length() - 4);
        sb.append(";");
        
        return DB.delete(sb.toString());
    }

    @Override
    public List<String> getAllUsers() {
        List<String> list = new ArrayList<>();
        List<List<String>> rs = DB.select("Korisnik",
                new String[]{"KorisnickoIme"},
                null,
                null,
                null);
        
        rs.forEach((l) -> {
            list.add(l.get(0));
        });
        
        return list;
    }

    @Override
    public boolean insertUser(String userName, String firstName, String lastName, String password, int adr) {
        if (!firstName.matches("[A-Z][a-z]*") ||
            !lastName.matches("[A-Z][a-z]*") ||
            !password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+-=]).{8,}$")) {
            return false;
        }
        
        CallableStatement cs = DB.call("spInsertUser",
                userName, firstName, lastName, password, adr, DB.outParams.Boolean, DB.outParams.String);
        
        try {
//            System.out.println(cs.getString(7));
            return cs.getBoolean(6);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean declareAdmin(String string) {
        CallableStatement cs = DB.call("spDeclareAdmin",
                string, DB.outParams.Boolean);
        
        try {
            return cs.getBoolean(2);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public int getSentPackages(String... strings) {
        String params[] = new String[strings.length];
        Arrays.fill(params, "Korisnik");
        
        List<List<String>> rs = DB.select("Paket",
                new String[]{"COUNT(*)"},
                params,
                strings,
                null);
        
        Arrays.fill(params, "KorisnickoIme");
        
        List<List<String>> users = DB.select("Korisnik",
                new String[]{"COUNT(*)"},
                params,
                strings,
                null);
        
        int cnt = Integer.parseInt(users.get(0).get(0));
        int ret = Integer.parseInt(rs.get(0).get(0));
        return cnt == 0 ? -1 : ret;
    }

}
