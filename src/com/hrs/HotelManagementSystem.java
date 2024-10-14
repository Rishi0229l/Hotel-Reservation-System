package com.hrs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelManagementSystem {

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db","root","Admin@5");
			
			
			while(true) {
				System.out.println();
				System.out.println("HOTEL MANAGEMENT SYSTEM");
				Scanner sc=new Scanner(System.in);
				System.out.println("1. Reserve a room");
				System.out.println("2. View Reservation");
				System.out.println("3. Get Room Number");
				System.out.println("4. Update Reservation");
				System.out.println("5. Delete Reservation");
				System.out.println("0. Exit");
				System.out.println("Choose an option: ");
				int choice=sc.nextInt();
				switch(choice) {
				    case 1:
				    	reserveRoom(con,sc);
				    	break;
				    case 2:
				    	viewReservation(con);
				    	break;
				    case 3:
				    	getRoomNumber(con,sc);
				    	break;
				    case 4:
				    	updateReservation(con,sc);
				    	break;
				    case 5:
				    	deleteReservation(con,sc);
				    	break;
				    case 0:
				    	exit();
				    	sc.close();
				    	return;
				    default:
				    	System.out.println("Invalid choice. Try Again...");
				}
			}
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	
	// Writing all the method's description from here
	
	private static void reserveRoom(Connection con,Scanner sc){
		try {
			System.out.print("Enter guest name: ");
			String guestName=sc.next();
			System.out.print("Enter room number: ");
			int roomNumber=sc.nextInt();
			System.out.print("Enter contact number: ");
			String contactNumber=sc.next();
			
			String sql="INSERT INTO reservations(guest_name,room_number,contact_number)"+"VALUES('"+guestName+"',"+roomNumber+",'"+contactNumber+"')";
			try(Statement stmt=con.createStatement()){
				int i=stmt.executeUpdate(sql);
				if(i>0)
					System.out.println("Reservation successful");
				else
					System.out.println("Reservation failed");
			}
		}
		catch(SQLException e) {
			System.out.println(e);
		}
	}

	private static void viewReservation(Connection con) {
		String sql="SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";
		try{
			Statement stmt=con.createStatement();
			ResultSet resultSet=stmt.executeQuery(sql);
			
			System.out.println("Current reservations: ");
			System.out.println("+----------------+-----------------+-------------+----------------+---------------------+");
			System.out.println("| reservation_id | guest_name      | room_number | contact_number | reservation_date    |");
			System.out.println("+----------------+-----------------+-------------+----------------+---------------------+");
			
			while(resultSet.next()) {
				int rId=resultSet.getInt("reservation_id");
				String gName=resultSet.getString("guest_name");
				int rNumber=resultSet.getInt("room_number");
				String cNumber=resultSet.getString("contact_number");
				String rDate=resultSet.getString("reservation_date");
				System.out.printf("| %-14d | %-15s | %-11d | %-14s | %-19s |",rId,gName,rNumber,cNumber,rDate);
				System.out.println();
				
//				try {
//					Thread.sleep(500);
//				}
//				catch(InterruptedException e){
//					System.out.println(e);
//				};
			}
			System.out.println("+----------------+-----------------+-------------+----------------+---------------------+");
		}
		catch(SQLException e) {
			System.out.println(e);
		}
	}
	
    private static void getRoomNumber(Connection con,Scanner sc){
    	System.out.print("Enter reservation id: ");
		int id=sc.nextInt();
		System.out.print("Enter guest name: ");
		String name=sc.next();
		
		String sql="SELECT room_number from reservations WHERE reservation_id="+id+" AND guest_name='"+name+"'";
		try {
			Statement stmt=con.createStatement();
			ResultSet resultSet=stmt.executeQuery(sql);
			
			if(resultSet.next()) {
				int rno=resultSet.getInt("room_number");
				System.out.println("\u001B[32m"+"The room number of reservation id "+id+" and guest name "+name+" is "+rno+"\u001B[0m");
			}else {
				System.out.println("\u001B[31m"+"Reservation not found for reservation id "+id+" and guest name "+name+"\u001B[0m");
			}
		}
		catch(SQLException e) {
			System.out.println(e);
		}
	}
	
	
    private static void updateReservation(Connection con,Scanner sc){
		System.out.print("Enter reservation Id: ");
		int id=sc.nextInt();
		
		if(!reservationExists(con,id)) {
			System.out.println("Reservation not found");
			return;
		}
		
		System.out.print("Enter new guest name: ");
		String newGuestName=sc.next();
		System.out.println("Enter new room number: ");
		int newRoomNumber=sc.nextInt();
		System.out.println("Enter new contact number: ");
		String newContactNumber=sc.next();
		
		String sql="UPDATE reservations SET guest_name='"+newGuestName+"', room_number="+newRoomNumber+", contact_number='"+newContactNumber+"' WHERE reservation_id="+id+"";
		
		try {
			Statement stmt=con.createStatement();
			int affectedRows=stmt.executeUpdate(sql);
			if(affectedRows>0)
				System.out.println("Reservation updated sucessfully!");
			else
				System.out.println("Reservation update failed");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean reservationExists(Connection con,int id) {
		try {
			String sql="SELECT reservation_id FROM reservations WHERE reservation_id="+id;
			Statement stmt=con.createStatement();
			ResultSet resultSet=stmt.executeQuery(sql);
			return resultSet.next();
		}
		catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
    private static void deleteReservation(Connection con,Scanner sc){
    	System.out.print("Enter reservation Id: ");
    	int id=sc.nextInt();
    	
    	if(!reservationExists(con,id)) {
    		System.out.println("Reservation not found");
			return;
    	}
    	
    	String sql="DELETE FROM reservations WHERE reservation_id="+id;
    	try{
    		Statement stmt=con.createStatement();
    		int a=stmt.executeUpdate(sql);
    		if(a>0)
    		    System.out.println("Resevation deleted successfully!");
    		else
    		    System.out.println("Reservation not deleted");
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    	}
	}
    
    
    private static void exit() throws InterruptedException{
    	System.out.print("Exiting System");
    	for(int i=0;i<3;i++) {
    		System.out.print(".");
    		Thread.sleep(500);
    	}
    	System.out.println("\nThank You for using Hotel Reservation System");
    }
}
