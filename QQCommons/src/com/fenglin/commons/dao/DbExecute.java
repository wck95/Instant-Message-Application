package com.fenglin.commons.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.fenglin.commons.utils.DBUtils;
import com.fenglin.commons.entity.BaseEntity;
import com.fenglin.commons.entity.User;
import com.fenglin.commons.model.Record;


public class DbExecute {

	public static int Update(String sql, Object obj) {
		int row = 0;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = DBUtils.getConnection();
			stmt = conn.prepareStatement(sql);
			row = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, stmt, conn);
		}
		return row;
	}
	
	public static int Insert(String sql, Object obj) {
		int row = 0;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = DBUtils.getConnection();
			stmt = conn.prepareStatement(sql);
			row = stmt.executeUpdate();
			System.out.println("insert.rows="+row);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, stmt, conn);
		}
		return row;
	}

	public static List<Record> executeQuery(String sql) {
		List<Record> result = new ArrayList<Record>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtils.getConnection();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			result = RecordBuilder.builde(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, stmt, conn);
		}
		return result;
	}
	
	public static Record findBy(String sql) {
		Record record = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtils.getConnection();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			List<Record> list =  RecordBuilder.builde(rs);
			if(list.size() > 0)record = list.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, stmt, conn);
		}
		return record;
	}

	private static void close(ResultSet resu, Statement stmt, Connection conn) {
		try {
			if (resu != null) resu.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	 
}
