package lin.leila.week2_nytimesearch;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by Leila on 2017/2/26.
 */

public class DatePickerFragment extends DialogFragment {

    public static final String KEY = "lin.leila.week2_nytimesearch";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        SharedPreferences spref;
        spref = getActivity().getSharedPreferences(KEY, Context.MODE_PRIVATE);

        // Activity needs to implement this interface
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, spref.getInt("dateYYYY", year), spref.getInt("dateMM", month), spref.getInt("dateDD", day));
    }


}