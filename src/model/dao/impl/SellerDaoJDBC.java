package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{

    private Connection conn;

    public SellerDaoJDBC(Connection conn){
        this.conn = conn;
    }

    @Override
    public void insert(Seller obj) {
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(
            "INSERT INTO seller "+
            "(Name, Email, BirthDate, BaseSalary, DepartmentId) "+
            "VALUES "+
            "(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, obj.getName());
            ps.setString(2, obj.getEmail());
            ps.setDate(3, new Date(obj.getBirthDate().getTime()));
            ps.setDouble(4, obj.getBaseSalary());
            ps.setInt(5, obj.getDepartment().getId());
            
            int rowsAfeccted = ps.executeUpdate();
            if(rowsAfeccted > 0){
                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()){
                    obj.setId(rs.getInt(1));
                }
                DB.closeResultSet(rs);
            }else{
                throw new DbException("Unexpected error! No rows affected!");
            }
        }catch(SQLException e){
            throw new DbException(e.getMessage());
        }finally{
            DB.closeStatement(ps);
        }
    }
 
    @Override
    public void update(Seller obj) {
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(
            "UPDATE seller "+
            "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "+
            "WHERE Id = ?");
            ps.setString(1, obj.getName());
            ps.setString(2, obj.getEmail());
            ps.setDate(3, new Date(obj.getBirthDate().getTime()));
            ps.setDouble(4, obj.getBaseSalary());
            ps.setInt(5, obj.getDepartment().getId());
            ps.setInt(6, obj.getId());
            ps.executeUpdate();
        }catch(SQLException e){
            throw new DbException(e.getMessage());
        }finally{
            DB.closeStatement(ps);
        }           
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
            st.setInt(1, id);
            st.executeUpdate();
        }catch(SQLException e){
            throw new DbException(e.getMessage());
        }finally{
            DB.closeStatement(st);
        }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement(
                "SELECT seller.*,department.Name as DepName "+
                "FROM seller INNER JOIN department "+
                "ON seller.DepartmentId = department.Id "+
                "WHERE seller.Id = ?");
            st.setInt(1, id);
            rs = st.executeQuery();
            if(rs.next()){
                Department dep = instantiateDepartment(rs);
                Seller seller = instantiateSeller(rs, dep);
                return seller;
            }
        }catch(SQLException e){
            throw new DbException(e.getMessage());
        }finally{
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
        return null;
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(
                "SELECT seller.*,department.Name as DepName "+
                "FROM seller INNER JOIN department "+
                "ON seller.DepartmentId = department.Id "+
                "ORDER BY Name");
            rs = ps.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while(rs.next()){
                Department dep = map.get(rs.getInt("DepartmentId"));
                if(dep == null){
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }
                Seller seller = instantiateSeller(rs, dep);
                list.add(seller);
            }
            return list;
        }catch(SQLException e){
            throw new DbException(e.getMessage());
        }finally{
            DB.closeResultSet(rs);
            DB.closeStatement(ps);
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department){
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(
                "SELECT seller.*,department.Name as DepName "+
                "FROM seller INNER JOIN department "+
                "ON seller.DepartmentId = department.Id "+
                "WHERE DepartmentId = ? "+
                "ORDER BY Name");
            ps.setInt(1, department.getId());
            rs = ps.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while(rs.next()){
                Department dep = map.get(rs.getInt("DepartmentId"));
                if(dep == null){
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }
                Seller seller = instantiateSeller(rs, dep);
                list.add(seller);
            }
            return list;
        }catch(SQLException e){
            throw new DbException(e.getMessage());
        }finally{
            DB.closeResultSet(rs);
            DB.closeStatement(ps);
        }
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException{
        return new Department(rs.getInt("DepartmentId"), rs.getString("DepName"));
    }

    private Seller instantiateSeller(ResultSet rs, Department department) throws SQLException{
        return new Seller(rs.getInt("Id"), rs.getString("Name"), rs.getString("Email"), new java.util.Date(rs.getDate("BirthDate").getTime()), rs.getDouble("BaseSalary"), department);
    }
}