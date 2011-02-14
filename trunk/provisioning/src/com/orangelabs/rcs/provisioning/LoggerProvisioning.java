package com.orangelabs.rcs.provisioning;

import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.orangelabs.rcs.provider.settings.RcsSettings;

/**
 * End user profile parameters provisioning
 * 
 * @author jexa7410
 */
public class LoggerProvisioning extends Activity {
	/**
	 * Trace level
	 */
    private static final String[] TRACE_LEVEL = {
        "DEBUG", "INFO", "WARN", "ERROR", "FATAL" 
    };

    /**
	 * Content resolver
	 */
	private ContentResolver cr;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set layout
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.logger_provisioning);
        
        // Set database content resolver
        this.cr = getContentResolver();
        
		// Get settings from database
        Map<String, String> settings = RcsSettings.getInstance().dump();
        
        // Display logger parameters
    	CheckBox check = (CheckBox)this.findViewById(R.id.TraceActivation);
        check.setChecked(Boolean.parseBoolean(settings.get("TraceActivation")));
        
    	check = (CheckBox)this.findViewById(R.id.SipTraceActivation);
        check.setChecked(Boolean.parseBoolean(settings.get("SipTraceActivation")));

    	check = (CheckBox)this.findViewById(R.id.MediaTraceActivation);
        check.setChecked(Boolean.parseBoolean(settings.get("MediaTraceActivation")));

        Spinner spinner = (Spinner)findViewById(R.id.TraceLevel);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, TRACE_LEVEL);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (RcsSettings.getInstance().getImsAuhtenticationProcedure().equals(TRACE_LEVEL[0])) {
            spinner.setSelection(0);
        } else if (RcsSettings.getInstance().getImsAuhtenticationProcedure().equals(TRACE_LEVEL[1])){
            spinner.setSelection(1);
        } else if (RcsSettings.getInstance().getImsAuhtenticationProcedure().equals(TRACE_LEVEL[2])){
            spinner.setSelection(2);
        } else if (RcsSettings.getInstance().getImsAuhtenticationProcedure().equals(TRACE_LEVEL[3])){
            spinner.setSelection(3);
        } else if (RcsSettings.getInstance().getImsAuhtenticationProcedure().equals(TRACE_LEVEL[4])){
            spinner.setSelection(4);
        }
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater=new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_save:
		        // Save logger parameters
		        CheckBox check = (CheckBox)this.findViewById(R.id.TraceActivation);
				Provisioning.writeParameter(cr, "TraceActivation", Boolean.toString(check.isChecked()));

		        check = (CheckBox)this.findViewById(R.id.SipTraceActivation);
				Provisioning.writeParameter(cr, "SipTraceActivation", Boolean.toString(check.isChecked()));

		        check = (CheckBox)this.findViewById(R.id.MediaTraceActivation);
				Provisioning.writeParameter(cr, "MediaTraceActivation", Boolean.toString(check.isChecked()));

				Spinner spinner = (Spinner)findViewById(R.id.TraceLevel);
				String value = (String)spinner.getSelectedItem();
				Provisioning.writeParameter(cr, "TraceLevel", value);

		        Toast.makeText(this, getString(R.string.label_reboot_service), Toast.LENGTH_LONG).show();				
		        break;
		}
		return true;
	}
}
