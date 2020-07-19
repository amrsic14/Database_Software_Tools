/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acamr
 */
public class DB {

    public static long insert(String query) {
        try (Statement stat
                = DBConnection.getInstance().getConnection().createStatement()) {

            if (stat.executeUpdate(query, Statement.RETURN_GENERATED_KEYS) == 0) {
                return -1;
            }

            ResultSet generatedKeys = stat.getGeneratedKeys();
            generatedKeys.next();
            return generatedKeys.getLong(1);

        } catch (SQLException ex) {
//            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public static int insert(String table, String[] columns, String[] values) {
        StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO ");
        sb.append("[").append(table).append("] ");

        sb.append("(");
        for (String column : columns) {
            sb.append(column).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length() - 1);
        sb.append(") ");

        sb.append(" values (");
        for (String value : values) {
            sb.append("'").append(value).append("'").append(", ");
        }
        sb.delete(sb.length() - 2, sb.length() - 1);
        sb.append(");");

        return (int) insert(sb.toString());
    }

    public static int delete(String query) {
        try (Statement stat
                = DBConnection.getInstance().getConnection().createStatement()) {

            return stat.executeUpdate(query);

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public static int deleteAND(String table, String[] columns, String[] values) {
        StringBuilder sb = new StringBuilder();

        sb.append("DELETE FROM ");
        sb.append("[").append(table).append("] ");

        sb.append("WHERE ");
        for (int i = 0; i < columns.length; i++) {
            if (i != 0) {
                sb.append(" AND ");
            }

            if (values[i] != null) {
                sb.append(columns[i]).append("='").append(values[i]).append("'");
            } else {
                sb.append(columns[i]).append(" IS NULL ");
            }
        }
        sb.append(";");

        return delete(sb.toString());
    }
    
    public static int deleteOR(String table, String[] columns, String[] values) {
        StringBuilder sb = new StringBuilder();

        sb.append("DELETE FROM ");
        sb.append("[").append(table).append("] ");

        sb.append("WHERE ");
        for (int i = 0; i < columns.length; i++) {
            if (i != 0) {
                sb.append(" OR ");
            }

            if (values[i] != null) {
                sb.append(columns[i]).append("='").append(values[i]).append("'");
            } else {
                sb.append(columns[i]).append(" IS NULL ");
            }
        }
        sb.append(";");

        return delete(sb.toString());
    }

    public static List<List<String>> select(String query, int numOfColumns) {
        try (Statement stat
                = DBConnection.getInstance().getConnection().createStatement()) {

            ResultSet rs = stat.executeQuery(query);
            List<List<String>> list = new ArrayList<>();

            while (rs.next()) {
                List<String> strings = new ArrayList<>();
                for (int i = 0; i < numOfColumns; i++) {
                    strings.add(rs.getString(i + 1));
                }
                list.add(strings);
            }

//            if(list.isEmpty()) list.add(new ArrayList<>());
            return list;
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static List<List<String>> select(String table,
            String[] columns, String[] restrictions, String[] values,
            String additionlCommand) {

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        for (String column : columns) {
            sb.append(column).append(",");
        }
        sb.setLength(sb.length() - 1);

        sb.append(" FROM ");
        sb.append("[").append(table).append("] ");

        if (restrictions != null && restrictions.length > 0) {
            sb.append("WHERE ");
            for (int i = 0; i < restrictions.length; i++) {
                if (i != 0) {
                    sb.append(" OR ");
                }
                if (values[i] != null) {
                    sb.append(restrictions[i]).append("='").append(values[i]).append("'");
                } else {
                    sb.append(restrictions[i]).append(" IS NULL ");
                }
            }
        }

        if(additionlCommand != null) sb.append(additionlCommand);
        sb.append(";");
        
        return select(sb.toString(), columns.length);
    }

    public enum outParams {
        Integer(java.sql.Types.INTEGER),
        Boolean(java.sql.Types.BOOLEAN),
        Double(java.sql.Types.DOUBLE),
        String(java.sql.Types.VARCHAR),
        DateTime(java.sql.Types.TIMESTAMP),
        Date(java.sql.Types.DATE);

        private final int code;

        outParams(int code) {
            this.code = code;
        }

        public int getValue() {
            return code;
        }
    }

    public static CallableStatement call(String name, Object... parameters) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append("{call ").append(name).append(" (");
            sb.append(parameters.length <= 0 ? "" :
                    "?" + new String(new char[parameters.length - 1]).replace("\0", ",?"));
            sb.append(")}");

            CallableStatement stmt
                    = DBConnection.getInstance().getConnection().prepareCall(sb.toString());

            int i = 0;
            for (Object obj : parameters) {
                i++;
                if (obj instanceof Integer) {
//                    System.out.println((Integer) obj);
                    stmt.setInt(i, (Integer) obj);
                } else if (obj instanceof String) {
//                    System.out.println((String) obj);
                    stmt.setString(i, (String) obj);
                } else if (obj instanceof BigDecimal) {
//                    System.out.println(((BigDecimal) obj).doubleValue());
                    stmt.setDouble(i, ((BigDecimal) obj).doubleValue());
                } else if (obj instanceof outParams) {
                    stmt.registerOutParameter(i, ((outParams)obj).getValue());
                } else {
                    throw new UnknownError("Type unknown error.");
                }
            }
            
            stmt.execute();

            return stmt;
        } catch (SQLException e) {
//            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, e);
            System.out.println(e);
            return null;
        }
    }
    
    public static boolean update(String query) {
        try (Statement stat
                = DBConnection.getInstance().getConnection().createStatement()) {

            return stat.executeUpdate(query, Statement.RETURN_GENERATED_KEYS) != 0;

        } catch (SQLException ex) {
//            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static boolean update(String tableName, String[] columnNames,
            String[] values, String[] restrictions, String[] restValues) {

        StringBuilder sb = new StringBuilder();

        sb.append("UPDATE ");
        sb.append("[").append(tableName).append("]");
        sb.append(" SET ");
        for (int i = 0; i < columnNames.length; i ++) {
            sb.append(columnNames[i]).append("='").append(values[i]).append("', ");
        }
        sb.setLength(sb.length() - 2);
        if (restrictions != null && restrictions.length > 0) {
            sb.append(" WHERE ");
            for (int i = 0 ; i < restrictions.length; i ++) {
                if (i != 0) {
                    sb.append(" AND ");
                }
                if ("null".equals(values[i])) {
                    sb.append(restrictions[i]).append(" IS NULL ");
                } else {
                    sb.append(restrictions[i]).append(" = '").append(restValues[i]).append("'");
                }

            }
        }
        sb.append(";");
        return update(sb.toString());
    }
    
}
