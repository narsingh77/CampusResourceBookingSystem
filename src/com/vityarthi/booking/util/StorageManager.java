package com.vityarthi.booking.util;

import com.vityarthi.booking.model.Reservation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * File I/O persistence manager for backing up reservations.
 */
public class StorageManager {
    private static final String DATA_DIR = "data";
    private static final String RESERVATIONS_FILE = DATA_DIR + File.separator + "reservations.dat";

    public static void saveReservations(List<Reservation> reservations) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RESERVATIONS_FILE))) {
                oos.writeObject(reservations);
            }
        } catch (IOException e) {
            System.err.println("[StorageManager] Warning: Could not save reservations: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Reservation> loadReservations() {
        File file = new File(RESERVATIONS_FILE);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Reservation>) ois.readObject();
        } catch (Exception e) {
            System.err.println("[StorageManager] Notice: Starting with fresh state (" + e.getMessage() + ")");
            return null;
        }
    }
}
