package com.example.ecoventur.ui.ecorewards.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ecoventur.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Random;

public class QRDisplayFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();

        // Set the title for the Toolbar in the hosting activity
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("QR Code");
        }
        return inflater.inflate(R.layout.fragment_qr_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView qrImageView = view.findViewById(R.id.qr);

        // Generate a random string for QR code content
        String randomContent = generateRandomString();

        // Generate QR code bitmap
        Bitmap bitmap = generateQRCode(randomContent, requireContext());

        // Set the generated QR code bitmap to the ImageView
        qrImageView.setImageBitmap(bitmap);
    }

    private String generateRandomString() {
        // Generate a random string or code for QR content
        // For example, you can use a random number or any unique identifier
        Random random = new Random();
        return String.valueOf(random.nextInt(1000)); // Change this according to your requirements
    }

    private Bitmap generateQRCode(String content, Context context) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ?
                            context.getResources().getColor(R.color.black) :
                            context.getResources().getColor(R.color.white));
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}