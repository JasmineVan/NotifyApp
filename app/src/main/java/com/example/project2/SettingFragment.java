package com.example.project2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingFragment extends Fragment {

    private View view;
    private Spinner languageSpinner, fontSnipper, soundSnipper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_setting, container, false);
        //language spinner
        languageSpinner = view.findViewById(R.id.settingLanguages);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
        //font spinner
        fontSnipper = view.findViewById(R.id.settingFonts);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getContext(),
                R.array.fonts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSnipper.setAdapter(adapter1);
        //ringtone spinner
        soundSnipper = view.findViewById(R.id.settingSounds);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getContext(),
                R.array.ringtones, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        soundSnipper.setAdapter(adapter2);
        return view;
    }
}