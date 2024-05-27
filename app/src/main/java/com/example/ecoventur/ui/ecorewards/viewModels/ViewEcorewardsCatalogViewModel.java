package com.example.ecoventur.ui.ecorewards.viewModels;

import androidx.lifecycle.ViewModel;

public class ViewEcorewardsCatalogViewModel extends ViewModel {
    private String selectedDocId;

    // Getter and Setter for selectedVoucherId

    public String getSelectedDocId() {
        return selectedDocId;
    }

    public void setSelectedDocId(String selectedDocId) {
        this.selectedDocId = selectedDocId;
    }
}
