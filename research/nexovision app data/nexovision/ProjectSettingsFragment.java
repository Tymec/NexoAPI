package eu.nexwell.android.nexovision;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import eu.nexwell.android.nexovision.misc.XMLProject;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.VideophoneIP;
import java.io.File;
import java.util.ArrayList;
import nexovision.android.nexwell.eu.nexovision.R;

public class ProjectSettingsFragment extends PreferenceFragment {
    private Context _context;
    private Editor editor;
    private DynamicListPreferenceWithCurrentEntry project_current;
    private SharedPreferences sharedPrefs;

    /* renamed from: eu.nexwell.android.nexovision.ProjectSettingsFragment$1 */
    class C20541 implements OnPreferenceChangeListener {
        C20541() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (!XMLProject.defaultProject.equals(PreferenceManager.getDefaultSharedPreferences(ProjectSettingsFragment.this._context).getString("pref_systemprojectspath", XMLProject.defaultProjectsPath) + File.separator + ((String) newValue) + File.separator + "nexoproject.xml")) {
                XMLProject.defaultProject = PreferenceManager.getDefaultSharedPreferences(ProjectSettingsFragment.this._context).getString("pref_systemprojectspath", XMLProject.defaultProjectsPath) + File.separator + ((String) newValue) + File.separator + "nexoproject.xml";
                if (!new File(XMLProject.defaultProject).exists()) {
                    XMLProject.defaultProject = PreferenceManager.getDefaultSharedPreferences(ProjectSettingsFragment.this._context).getString("pref_systemprojectspath", XMLProject.defaultProjectsPath) + File.separator + "default" + File.separator + "nexoproject.xml";
                }
                if (new File(XMLProject.defaultProject).exists()) {
                    PreferenceManager.getDefaultSharedPreferences(ProjectSettingsFragment.this._context).edit().putString("pref_systemlastproject", XMLProject.defaultProject).commit();
                    new BackgroundTask().execute(new Void[0]);
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectSettingsFragment$2 */
    class C20552 implements OnPreferenceClickListener {
        C20552() {
        }

        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent().setClass(MainActivity.getContext(), ProjectsListActivity.class);
            intent.addFlags(67108864);
            ProjectSettingsFragment.this.startActivityForResult(intent, 0);
            return true;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectSettingsFragment$3 */
    class C20563 implements OnPreferenceClickListener {
        C20563() {
        }

        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent().setClass(MainActivity.getContext(), NexoResourcesActivity.class);
            intent.addFlags(67108864);
            ProjectSettingsFragment.this.startActivityForResult(intent, 0);
            return true;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectSettingsFragment$4 */
    class C20574 implements OnPreferenceClickListener {
        C20574() {
        }

        public boolean onPreferenceClick(Preference preference) {
            NVModel.CURR_ELEMENT = null;
            Intent intent = new Intent().setClass(MainActivity.getContext(), EditorLogicActivity.class);
            intent.addFlags(67108864);
            ProjectSettingsFragment.this.startActivityForResult(intent, 0);
            return true;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectSettingsFragment$5 */
    class C20585 implements OnPreferenceClickListener {
        C20585() {
        }

        public boolean onPreferenceClick(Preference preference) {
            NVModel.CURR_ELEMENT = null;
            Intent intent = new Intent().setClass(MainActivity.getContext(), EditorCameraActivity.class);
            intent.addFlags(67108864);
            ProjectSettingsFragment.this.startActivityForResult(intent, 0);
            return true;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectSettingsFragment$6 */
    class C20596 implements OnPreferenceClickListener {
        C20596() {
        }

        public boolean onPreferenceClick(Preference preference) {
            NVModel.CURR_ELEMENT = null;
            Intent intent = new Intent().setClass(MainActivity.getContext(), EditorVideophoneActivity.class);
            intent.addFlags(67108864);
            ProjectSettingsFragment.this.startActivityForResult(intent, 0);
            return true;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectSettingsFragment$7 */
    class C20607 implements OnPreferenceClickListener {
        C20607() {
        }

        public boolean onPreferenceClick(Preference preference) {
            NVModel.CURR_ELEMENT = null;
            Intent intent = new Intent().setClass(MainActivity.getContext(), EditorSetActivity.class);
            intent.addFlags(67108864);
            ProjectSettingsFragment.this.startActivityForResult(intent, 0);
            return true;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectSettingsFragment$8 */
    class C20618 implements OnPreferenceClickListener {
        C20618() {
        }

        public boolean onPreferenceClick(Preference preference) {
            NVModel.CURR_ELEMENT = null;
            Intent intent = new Intent().setClass(MainActivity.getContext(), EditorGeolocationPointActivity.class);
            intent.addFlags(67108864);
            ProjectSettingsFragment.this.startActivityForResult(intent, 0);
            return true;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectSettingsFragment$9 */
    class C20629 implements OnPreferenceClickListener {
        C20629() {
        }

        public boolean onPreferenceClick(Preference preference) {
            if (!(XMLProject.defaultProject == null || XMLProject.defaultProject.isEmpty() || !XMLProject.write(XMLProject.defaultProject))) {
                Snackbar.make(ProjectSettingsFragment.this.getView(), MainActivity.getContext().getString(R.string.XMLProjectWriter_SaveOKMessage), 0).show();
            }
            return true;
        }
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        private BackgroundTask() {
        }

        protected void onPreExecute() {
            this.dialog = ProgressDialog.show(ProjectSettingsFragment.this._context, "", ProjectSettingsFragment.this._context.getString(R.string.loading_project), true);
        }

        protected Void doInBackground(Void... params) {
            XMLProject.parse(XMLProject.defaultProject, ProjectSettingsFragment.this._context);
            ArrayList<IElement> vidips = NVModel.getElementsByType(NVModel.EL_TYPE_VIDEOPHONE);
            if (vidips.size() > 0) {
                ProjectSettingsFragment.this.editor.putString("pref_VidIP", ((VideophoneIP) vidips.get(0)).getAddress());
                ProjectSettingsFragment.this.editor.putString("pref_VIdSIP", ((VideophoneIP) vidips.get(0)).getSipProxy());
                ProjectSettingsFragment.this.editor.commit();
            } else {
                ProjectSettingsFragment.this.editor.putString("pref_VidIP", "");
                ProjectSettingsFragment.this.editor.putString("pref_VIdSIP", "");
                ProjectSettingsFragment.this.editor.commit();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            this.dialog.dismiss();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.project_preferences);
        this._context = getActivity();
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this._context);
        this.editor = this.sharedPrefs.edit();
        this.project_current = (DynamicListPreferenceWithCurrentEntry) findPreference("pref_currentproject");
        ArrayList<String> projects = XMLProject.getProjectsList(this._context, false);
        CharSequence[] projects_cs = (CharSequence[]) projects.toArray(new CharSequence[projects.size()]);
        this.project_current.setEntryValues(projects_cs);
        this.project_current.setEntries(projects_cs);
        this.project_current.setValue(new File(XMLProject.defaultProject).getParentFile().getName());
        this.project_current.setSummary(null);
        this.project_current.setOnPreferenceChangeListener(new C20541());
        findPreference("pref_manageproject").setOnPreferenceClickListener(new C20552());
        findPreference("pref_projectimport").setOnPreferenceClickListener(new C20563());
        findPreference("pref_projectaddlogic").setOnPreferenceClickListener(new C20574());
        findPreference("pref_projectaddvideocam").setOnPreferenceClickListener(new C20585());
        findPreference("pref_projectaddvideophone").setOnPreferenceClickListener(new C20596());
        findPreference("pref_projectaddplace").setOnPreferenceClickListener(new C20607());
        findPreference("pref_projectaddgeolocpoint").setOnPreferenceClickListener(new C20618());
        findPreference("pref_projectsave").setOnPreferenceClickListener(new C20629());
    }

    public void onResume() {
        super.onResume();
        ArrayList<String> projects = XMLProject.getProjectsList(this._context, false);
        CharSequence[] projects_cs = (CharSequence[]) projects.toArray(new CharSequence[projects.size()]);
        this.project_current.setEntryValues(projects_cs);
        this.project_current.setEntries(projects_cs);
    }

    public void onDetach() {
        super.onDetach();
    }
}
