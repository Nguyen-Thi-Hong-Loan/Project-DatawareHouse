package test;

import java.util.Scanner;

import modal.Download;

public class TestDW {
	public static void main(String[] args) {
		Download download = new Download();
		Scanner sc = new Scanner(System.in);
		System.out.println("Nhập id config cần download: ");
		int id = sc.nextInt();
		System.out.println(download.saveDataFromFTPToLocal(id));
		System.out.println("done");

	}
}
