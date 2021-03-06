package com.example.pavel.translator;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;


public class TranslatorFragment extends Fragment {

    public ImageButton BtnSwap;
    public Spinner SpnFrom;
    public Spinner SpnOn;
    public TextView TextViewString;
    public EditText EditTextString;
    public String BROADCAST_ACTION = "Activity_broadcast";
    public BroadcastReceiver Broadcast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.translator, null);

        TextViewString = (TextView) view.findViewById(R.id.textViewString);
        EditTextString = (EditText) view.findViewById(R.id.editTextString);

        SpnFrom = (Spinner) view.findViewById(R.id.spn_language_from);
        SpnOn = (Spinner) view.findViewById(R.id.spn_language_on);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.row_spenner, R.id.text_row, MyActivity.getLangs());

        SpnFrom.setAdapter(adapter);
        SpnOn.setAdapter(adapter);

        SpnFrom.setSelection(MyActivity.getPositionLand("Русский"));
        SpnOn.setSelection(MyActivity.getPositionLand("Английский"));
        SpnFrom.setPrompt("Выберите язык ввода");
        SpnOn.setPrompt("Выберите язык перевода");

        SpnOn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sendRequest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BtnSwap = (ImageButton) view.findViewById(R.id.btn_swap);
        BtnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int from = SpnFrom.getSelectedItemPosition();
                int on = SpnOn.getSelectedItemPosition();
                SpnFrom.setSelection(on);
                SpnOn.setSelection(from);
            }
        });

        EditTextString.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(EditTextString.getWindowToken(), 0);
                }
                sendRequest();
                return false;
            }
        });

        // создаем фильтр для BroadcastReceiver
        IntentFilter filter = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        getActivity().registerReceiver(Broadcast, filter);

        return view;
    }

    public String parseSpinner() {
        String from = SpnFrom.getSelectedItem().toString();
        String on = SpnOn.getSelectedItem().toString();
        return MyActivity.getReductions(from) + "-" + MyActivity.getReductions(on);
    }


    public void sendRequest() {
        String dirs = parseSpinner();

        String text = EditTextString.getText().toString();
        if (!text.equals("")) {
            getActivity().startService(new Intent(getActivity(), TranslatorService.class)
                    .putExtra("COMMAND", 2)
                    .putExtra("TEXT", text)
                    .putExtra("DIRS", dirs));
        }
        else {
            Log.d("SendRequest", "Пустая строка");
        }


    }

}
