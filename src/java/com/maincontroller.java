package com.example.demo2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class maincontroller {
    @FXML
    private TextField idProdukField; // Input field untuk ID produk
    @FXML
    private TextField namaProdukField; // Input field untuk nama produk
    @FXML
    private TableView<Product> tableView; // Tabel untuk menampilkan data
    @FXML
    private TableColumn<Product, Integer> columnId; // Kolom untuk ID produk
    @FXML
    private TableColumn<Product, String> columnNama; // Kolom untuk nama produk

    private ObservableList<Product> productList; // Daftar produk untuk ditampilkan di tabel
    private Connection connection; // Koneksi ke database SQLite

    /**
     * Dipanggil saat aplikasi diinisialisasi.
     */
    public void initialize() {
        connectDatabase(); // Menghubungkan ke database
        productList = FXCollections.observableArrayList(); // Membuat list untuk data tabel
        columnId.setCellValueFactory(new PropertyValueFactory<>("id")); // Menghubungkan kolom ID dengan model
        columnNama.setCellValueFactory(new PropertyValueFactory<>("nama")); // Menghubungkan kolom Nama dengan model
        loadTableData(); // Mengambil data dari database
        tableView.setItems(productList); // Menampilkan data ke tabel
    }

    /**
     * Menghubungkan ke database SQLite dan membuat tabel jika belum ada.
     */
    private void connectDatabase() {
        try {
            // Path database SQLite
            connection = DriverManager.getConnection("jdbc:sqlite:products.db");
            System.out.println("Koneksi ke database berhasil!");

            // Membuat tabel jika belum ada
            String createTableQuery = "CREATE TABLE IF NOT EXISTS products (" +
                    "id INTEGER PRIMARY KEY, " +
                    "nama TEXT)";
            connection.createStatement().execute(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Gagal menghubungkan ke database.");
        }
    }

    /**
     * Memuat data dari database ke dalam tabel.
     */
    private void loadTableData() {
        productList.clear(); // Menghapus data lama di tabel
        try {
            String query = "SELECT * FROM products";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            // Menambahkan data dari database ke ObservableList
            while (rs.next()) {
                productList.add(new Product(rs.getInt("id"), rs.getString("nama")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Menambahkan data baru ke database.
     */
    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            // Query untuk memasukkan data
            String insertQuery = "INSERT INTO products (id, nama) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(insertQuery);
            ps.setInt(1, Integer.parseInt(idProdukField.getText())); // Mengambil ID dari input field
            ps.setString(2, namaProdukField.getText()); // Mengambil Nama dari input field
            ps.executeUpdate(); // Menjalankan query
            loadTableData(); // Memuat ulang data ke tabel
            clearFields(); // Mengosongkan input field
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Memperbarui data yang ada di database.
     */
    @FXML
    private void handleUpdate(ActionEvent event) {
        try {
            // Query untuk memperbarui data
            String updateQuery = "UPDATE products SET nama = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(updateQuery);
            ps.setString(1, namaProdukField.getText()); // Mengambil Nama dari input field
            ps.setInt(2, Integer.parseInt(idProdukField.getText())); // Mengambil ID dari input field
            ps.executeUpdate(); // Menjalankan query
            loadTableData(); // Memuat ulang data ke tabel
            clearFields(); // Mengosongkan input field
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Menghapus data dari database.
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        try {
            // Query untuk menghapus data
            String deleteQuery = "DELETE FROM products WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(deleteQuery);
            ps.setInt(1, Integer.parseInt(idProdukField.getText())); // Mengambil ID dari input field
            ps.executeUpdate(); // Menjalankan query
            loadTableData(); // Memuat ulang data ke tabel
            clearFields(); // Mengosongkan input field
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mengosongkan input field.
     */
    private void clearFields() {
        idProdukField.clear();
        namaProdukField.clear();
    }
}