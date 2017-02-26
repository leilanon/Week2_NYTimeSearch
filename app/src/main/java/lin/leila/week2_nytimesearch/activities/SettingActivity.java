package lin.leila.week2_nytimesearch.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

import lin.leila.week2_nytimesearch.DatePickerFragment;
import lin.leila.week2_nytimesearch.R;

/**
 * Created by Leila on 2017/2/26.
 */

public class SettingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ArrayAdapter adapter;
    Spinner spinner;
    CheckBox chkArts;
    CheckBox chkSports;
    CheckBox chkStyle;
    EditText etDate;
    int dateYYYY, dateMM, dateDD;
    Button btnSave;

    SharedPreferences spref;
    SharedPreferences.Editor editor;

    public static final String KEY = "lin.leila.week2_nytimesearch";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spref = getSharedPreferences(KEY, Context.MODE_PRIVATE);
        editor = spref.edit();


        findView();
        init();
        etDate.setText(spref.getInt("dateDD", 1) + "/" +
                spref.getInt("dateMM", 1) + "/" +
                spref.getInt("dateYYYY", 2000));

        dateDD = spref.getInt("dateDD", 1);
        dateMM = spref.getInt("dateMM", 1);
        dateYYYY = spref.getInt("dateYYYY", 2000);

        spinner.setSelection(spref.getInt("sortOption", 0));
        chkArts.setChecked(spref.getBoolean("deskArtsChk", false));
        chkSports.setChecked(spref.getBoolean("deskSportsChk", false));
        chkStyle.setChecked(spref.getBoolean("deskStyleChk", false));
    }

    public void findView(){
        etDate = (EditText) findViewById(R.id.etDate);
        spinner = (Spinner) findViewById(R.id.spSort);
        chkArts = (CheckBox) findViewById(R.id.chkArts);
        chkSports = (CheckBox) findViewById(R.id.chkSports);
        chkStyle = (CheckBox) findViewById(R.id.chkStyle);
        btnSave = (Button) findViewById(R.id.btnSave);
    }

    public void init(){
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        adapter = ArrayAdapter.createFromResource(this, R.array.sort_array, R.layout.spinner_item);
        spinner.setAdapter(adapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("sortOption", spinner.getSelectedItemPosition());
                editor.putBoolean("deskArtsChk", chkArts.isChecked());
                editor.putBoolean("deskSportsChk", chkSports.isChecked());
                editor.putBoolean("deskStyleChk", chkStyle.isChecked());

                editor.putInt("dateDD", dateDD);
                editor.putInt("dateMM", dateMM);
                editor.putInt("dateYYYY", dateYYYY);
                editor.apply();
                SettingActivity.this.finish();
            }
        });
    }

//    CompoundButton.OnCheckedChangeListener chkListener = new CompoundButton.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            switch(buttonView.getId()) {
//                case R.id.chkArts:
//                    spref.edit().putBoolean("deskArtsChk", isChecked).apply();
//                    break;
//                case R.id.chkSports:
//                    spref.edit().putBoolean("deskSportsChk", isChecked).apply();
//                    break;
//                case R.id.chkStyle:
//                    spref.edit().putBoolean("deskStyleChk", isChecked).apply();
//                    break;
//            }
//        }
//    };
    // attach to an onclick handler to show the date picker

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        dateDD = dayOfMonth;
        dateMM = monthOfYear;
        dateYYYY = year;

        etDate.setText(dateDD + "/" + dateMM + "/" + dateYYYY);
    }
}
