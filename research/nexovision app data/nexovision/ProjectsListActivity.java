package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import eu.nexwell.android.nexovision.CheckListAdapter.CheckListAdapterListener;
import eu.nexwell.android.nexovision.misc.XMLProject;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import nexovision.android.nexwell.eu.nexovision.R;

public class ProjectsListActivity extends AppCompatActivity implements CheckListAdapterListener {
    private static Context context;
    private static FloatingActionButton fab;
    public static Handler handler;
    private CheckListAdapter adapter;
    private ListView listProjects;
    private Intent mIntent;
    private File mParentPath;
    private EditText newProjectNameInput;
    private AlertDialog newProjectNameInputDialog;
    private ArrayList<String> projects;
    private EditText receiveProjectInput;
    private AlertDialog receiveProjectInputDialog;
    private AlertDialog removeProjectsDialog;
    private EditText renameProjectNameInput;
    private AlertDialog renameProjectNameInputDialog;

    /* renamed from: eu.nexwell.android.nexovision.ProjectsListActivity$1 */
    class C20631 implements OnClickListener {
        C20631() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            String newProjectName = ProjectsListActivity.this.newProjectNameInput.getText().toString();
            if (newProjectName != null && !newProjectName.isEmpty()) {
                ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ProjectsListActivity.this.findViewById(16908290)).getChildAt(0);
                if (new File(PreferenceManager.getDefaultSharedPreferences(ProjectsListActivity.this).getString("pref_systemprojectspath", XMLProject.defaultProjectsPath) + File.separator + newProjectName + File.separator + "nexoproject.xml").exists()) {
                    Snackbar.make(viewGroup, ProjectsListActivity.getContext().getString(R.string.ConnectionActivity_NewProjectErrMessage), 0).show();
                } else if (XMLProject.newProject(PreferenceManager.getDefaultSharedPreferences(ProjectsListActivity.this).getString("pref_systemprojectspath", XMLProject.defaultProjectsPath) + File.separator + newProjectName + File.separator + "nexoproject.xml")) {
                    Snackbar.make(viewGroup, ProjectsListActivity.getContext().getString(R.string.ConnectionActivity_NewProjectCreatedMessage), 0).show();
                    ProjectsListActivity.this.projects = XMLProject.getProjectsList(ProjectsListActivity.context, false);
                    ProjectsListActivity.this.adapter = new CheckListAdapter(ProjectsListActivity.getContext(), ProjectsListActivity.this.projects, ProjectsListActivity.this.projects, true);
                    Iterator<String> itrp = ProjectsListActivity.this.projects.iterator();
                    while (itrp.hasNext()) {
                        ProjectsListActivity.this.adapter.setItemChecked(ProjectsListActivity.this.projects.indexOf((String) itrp.next()), false);
                    }
                    ProjectsListActivity.this.adapter.setCheckListAdapterListener(ProjectsListActivity.this);
                    ProjectsListActivity.this.listProjects.setAdapter(ProjectsListActivity.this.adapter);
                    ProjectsListActivity.this.invalidateOptionsMenu();
                } else {
                    Snackbar.make(viewGroup, ProjectsListActivity.getContext().getString(R.string.ConnectionActivity_NewProjectErr2Message), 0).show();
                }
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectsListActivity$2 */
    class C20642 implements OnClickListener {
        C20642() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectsListActivity$3 */
    class C20653 implements OnClickListener {
        C20653() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            String renameProjectName = ProjectsListActivity.this.renameProjectNameInput.getText().toString();
            if (renameProjectName != null && !renameProjectName.isEmpty()) {
                ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ProjectsListActivity.this.findViewById(16908290)).getChildAt(0);
                File from = new File(PreferenceManager.getDefaultSharedPreferences(ProjectsListActivity.this).getString("pref_systemprojectspath", XMLProject.defaultProjectsPath) + File.separator + ((String) ProjectsListActivity.this.projects.get(ProjectsListActivity.this.adapter.getCheckedItemPositions().keyAt(0))));
                File to = new File(PreferenceManager.getDefaultSharedPreferences(ProjectsListActivity.this).getString("pref_systemprojectspath", XMLProject.defaultProjectsPath) + File.separator + renameProjectName);
                if (from.exists() && !to.exists() && from.renameTo(to)) {
                    Snackbar.make(viewGroup, String.format(ProjectsListActivity.getContext().getString(R.string.ConnectionActivity_ProjectRenamedMessage), new Object[]{renameOldProjectName, renameProjectName}), 0).show();
                    ProjectsListActivity.this.projects = XMLProject.getProjectsList(ProjectsListActivity.context, false);
                    ProjectsListActivity.this.adapter = new CheckListAdapter(ProjectsListActivity.getContext(), ProjectsListActivity.this.projects, ProjectsListActivity.this.projects, true);
                    Iterator<String> itrp = ProjectsListActivity.this.projects.iterator();
                    while (itrp.hasNext()) {
                        ProjectsListActivity.this.adapter.setItemChecked(ProjectsListActivity.this.projects.indexOf((String) itrp.next()), false);
                    }
                    ProjectsListActivity.this.adapter.setCheckListAdapterListener(ProjectsListActivity.this);
                    ProjectsListActivity.this.listProjects.setAdapter(ProjectsListActivity.this.adapter);
                    ProjectsListActivity.this.invalidateOptionsMenu();
                    return;
                }
                Snackbar.make(viewGroup, ProjectsListActivity.getContext().getString(R.string.ConnectionActivity_ProjectRenameErrMessage), 0).show();
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectsListActivity$4 */
    class C20664 implements OnClickListener {
        C20664() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectsListActivity$5 */
    class C20675 implements OnClickListener {
        C20675() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ProjectsListActivity.this.findViewById(16908290)).getChildAt(0);
            boolean delFail = false;
            String projectsRemovedList = "";
            String projectsNotRemovedList = "";
            for (int i = 0; i < ProjectsListActivity.this.adapter.getCheckedItemPositions().size(); i++) {
                int key = ProjectsListActivity.this.adapter.getCheckedItemPositions().keyAt(i);
                if (ProjectsListActivity.this.adapter.getCheckedItemPositions().get(key)) {
                    String fileName = (String) ProjectsListActivity.this.projects.get(key);
                    Log.d("ProjectsListActivity", "REMOVE: " + fileName);
                    if (XMLProject.deleteRecursive(new File(PreferenceManager.getDefaultSharedPreferences(ProjectsListActivity.this).getString("pref_systemprojectspath", XMLProject.defaultProjectsPath) + File.separator + fileName))) {
                        if (!projectsRemovedList.isEmpty()) {
                            projectsRemovedList = projectsRemovedList + ", ";
                        }
                        projectsRemovedList = projectsRemovedList + fileName;
                    } else {
                        delFail = true;
                        if (!projectsNotRemovedList.isEmpty()) {
                            projectsNotRemovedList = projectsNotRemovedList + ", ";
                        }
                        projectsNotRemovedList = projectsNotRemovedList + fileName;
                    }
                }
            }
            if (delFail) {
                Snackbar.make(viewGroup, String.format(ProjectsListActivity.getContext().getString(R.string.ProjectsListActivity_RemoveProjects_Dialog_PostToast_Fail), new Object[]{projectsNotRemovedList}), 0).show();
                return;
            }
            Snackbar.make(viewGroup, String.format(ProjectsListActivity.getContext().getString(R.string.ProjectsListActivity_RemoveProjects_Dialog_PostToast_Success), new Object[]{projectsRemovedList}), 0).show();
            ProjectsListActivity.this.projects = XMLProject.getProjectsList(ProjectsListActivity.context, false);
            ProjectsListActivity.this.adapter = new CheckListAdapter(ProjectsListActivity.getContext(), ProjectsListActivity.this.projects, ProjectsListActivity.this.projects, true);
            Iterator<String> itrp = ProjectsListActivity.this.projects.iterator();
            while (itrp.hasNext()) {
                ProjectsListActivity.this.adapter.setItemChecked(ProjectsListActivity.this.projects.indexOf((String) itrp.next()), false);
            }
            ProjectsListActivity.this.adapter.setCheckListAdapterListener(ProjectsListActivity.this);
            ProjectsListActivity.this.listProjects.setAdapter(ProjectsListActivity.this.adapter);
            ProjectsListActivity.this.invalidateOptionsMenu();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectsListActivity$6 */
    class C20686 implements OnClickListener {
        C20686() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectsListActivity$7 */
    class C20697 implements OnClickListener {
        C20697() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            String receiveProjectName = ProjectsListActivity.this.receiveProjectInput.getText().toString();
            if (receiveProjectName != null && !receiveProjectName.isEmpty()) {
                ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ProjectsListActivity.this.findViewById(16908290)).getChildAt(0);
                String receiveOldProjectName = ProjectsListActivity.this.mParentPath.getName();
                File from = ProjectsListActivity.this.mParentPath;
                File dir = new File(PreferenceManager.getDefaultSharedPreferences(ProjectsListActivity.this).getString("pref_systemprojectspath", XMLProject.defaultProjectsPath) + File.separator + receiveProjectName);
                dir.mkdirs();
                File to = new File(dir.getPath() + File.separator + "nexoproject.xml");
                if (from.exists() && !to.exists() && from.renameTo(to)) {
                    Snackbar.make(viewGroup, String.format(ProjectsListActivity.getContext().getString(R.string.ConnectionActivity_ProjectReceivedMessage), new Object[]{receiveProjectName}), 0).show();
                    ProjectsListActivity.this.projects = XMLProject.getProjectsList(ProjectsListActivity.context, false);
                    ProjectsListActivity.this.adapter = new CheckListAdapter(ProjectsListActivity.getContext(), ProjectsListActivity.this.projects, ProjectsListActivity.this.projects, true);
                    Iterator<String> itrp = ProjectsListActivity.this.projects.iterator();
                    while (itrp.hasNext()) {
                        ProjectsListActivity.this.adapter.setItemChecked(ProjectsListActivity.this.projects.indexOf((String) itrp.next()), false);
                    }
                    ProjectsListActivity.this.adapter.setCheckListAdapterListener(ProjectsListActivity.this);
                    ProjectsListActivity.this.listProjects.setAdapter(ProjectsListActivity.this.adapter);
                    ProjectsListActivity.this.invalidateOptionsMenu();
                    return;
                }
                Snackbar.make(viewGroup, ProjectsListActivity.getContext().getString(R.string.ConnectionActivity_ProjectReceiveErrMessage), 0).show();
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectsListActivity$8 */
    class C20708 implements OnClickListener {
        C20708() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ProjectsListActivity$9 */
    class C20719 implements OnItemClickListener {
        C20719() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            ProjectsListActivity.this.adapter.checkList.put(position, !Boolean.valueOf(ProjectsListActivity.this.adapter.checkList.get(position)).booleanValue());
            ProjectsListActivity.this.adapter.refresh();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_projectslist);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        Builder builder = new Builder(context);
        builder.setTitle(context.getString(R.string.ProjectsListActivity_NewProject_DialogTitle));
        this.newProjectNameInput = new EditText(context);
        this.newProjectNameInput.setGravity(17);
        builder.setView(this.newProjectNameInput);
        builder.setPositiveButton(context.getString(R.string.OK), new C20631());
        builder.setNegativeButton(context.getString(R.string.CANCEL), new C20642());
        this.newProjectNameInputDialog = builder.create();
        builder = new Builder(context);
        builder.setTitle(context.getString(R.string.ProjectsListActivity_RenameProject_DialogTitle));
        this.renameProjectNameInput = new EditText(context);
        this.renameProjectNameInput.setGravity(17);
        builder.setView(this.renameProjectNameInput);
        builder.setPositiveButton(context.getString(R.string.OK), new C20653());
        builder.setNegativeButton(context.getString(R.string.CANCEL), new C20664());
        this.renameProjectNameInputDialog = builder.create();
        builder = new Builder(context);
        builder.setTitle(context.getString(R.string.ProjectsListActivity_RemoveProjects_DialogTitle));
        builder.setPositiveButton(context.getString(R.string.YES), new C20675());
        builder.setNegativeButton(context.getString(R.string.NO), new C20686());
        this.removeProjectsDialog = builder.create();
        builder = new Builder(context);
        builder.setTitle(context.getString(R.string.ProjectsListActivity_ReceiveProject_DialogTitle));
        this.receiveProjectInput = new EditText(context);
        this.receiveProjectInput.setGravity(17);
        builder.setView(this.receiveProjectInput);
        builder.setPositiveButton(context.getString(R.string.OK), new C20697());
        builder.setNegativeButton(context.getString(R.string.CANCEL), new C20708());
        this.receiveProjectInputDialog = builder.create();
        this.listProjects = (ListView) findViewById(R.id.list_projects);
        this.projects = XMLProject.getProjectsList(context, false);
        this.adapter = new CheckListAdapter(getContext(), this.projects, this.projects, true);
        Iterator<String> itrp = this.projects.iterator();
        while (itrp.hasNext()) {
            this.adapter.setItemChecked(this.projects.indexOf((String) itrp.next()), false);
        }
        this.adapter.setCheckListAdapterListener(this);
        this.listProjects.setAdapter(this.adapter);
        this.listProjects.setOnItemClickListener(new C20719());
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ProjectsListActivity.this.newProjectNameInput.setText("");
                    ProjectsListActivity.this.newProjectNameInputDialog.getWindow().setSoftInputMode(4);
                    ProjectsListActivity.this.newProjectNameInputDialog.show();
                }
            });
        }
        handleViewIntent();
    }

    public static Context getContext() {
        return context;
    }

    public void onCheckChanged(boolean checked) {
        invalidateOptionsMenu();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manageproject, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        int sel = ((CheckListAdapter) this.listProjects.getAdapter()).getCheckedItemPositions().size();
        if (sel == 1) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);
        } else if (sel > 1) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            ViewGroup viewGroup = (ViewGroup) ((ViewGroup) findViewById(16908290)).getChildAt(0);
            String fileName = (String) this.projects.get(this.adapter.getCheckedItemPositions().keyAt(0));
            if (fileName == null || fileName.isEmpty()) {
                Snackbar.make(viewGroup, getContext().getString(R.string.ProjectsListActivity_ShareProjects_Message_NoFile), 0).show();
            } else {
                File projectDir = new File(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_systemprojectspath", XMLProject.defaultProjectsPath) + File.separator + fileName);
                if (projectDir.exists()) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.SEND_MULTIPLE");
                    intent.putExtra("android.intent.extra.SUBJECT", "Nexo '" + projectDir.getName() + "' project files");
                    intent.setType("*/*");
                    String authorities = getApplicationContext().getPackageName() + ".fileprovider";
                    ArrayList<Uri> files = new ArrayList();
                    for (File file : projectDir.listFiles()) {
                        if (file.isFile()) {
                            files.add(FileProvider.getUriForFile(context, authorities, file));
                        }
                    }
                    intent.putExtra("android.intent.extra.STREAM", files);
                    startActivityForResult(intent, 0);
                } else {
                    Snackbar.make(viewGroup, getContext().getString(R.string.ProjectsListActivity_ShareProjects_Message_NoFile), 0).show();
                }
            }
        } else if (id == R.id.action_rename) {
            this.renameProjectNameInput.setText((CharSequence) this.projects.get(this.adapter.getCheckedItemPositions().keyAt(0)));
            this.renameProjectNameInputDialog.getWindow().setSoftInputMode(4);
            this.renameProjectNameInputDialog.show();
        } else if (id == R.id.action_remove) {
            this.removeProjectsDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onNewIntent(Intent intent) {
        Log.d("MainActivity", "onNewIntent()");
        super.onNewIntent(intent);
        setIntent(intent);
        handleViewIntent();
    }

    private void handleViewIntent() {
        Log.d("MainActivity", "handleViewIntent()");
        Intent mIntent = getIntent();
        if (TextUtils.equals(mIntent.getAction(), "android.intent.action.VIEW")) {
            Log.d("MainActivity", "handleViewIntent(): Intent.ACTION_VIEW");
            Uri beamUri = mIntent.getData();
            if (TextUtils.equals(beamUri.getScheme(), "file")) {
                this.mParentPath = handleFileUri(beamUri);
                Log.d("MainActivity", "Got file: " + this.mParentPath.getPath());
            } else if (TextUtils.equals(beamUri.getScheme(), Param.CONTENT)) {
                this.mParentPath = handleContentUri(beamUri);
                Log.d("MainActivity", "Got content: " + this.mParentPath.getPath());
            }
            if (this.mParentPath != null && this.mParentPath.exists()) {
                String fileName = this.mParentPath.getName().split(".xml")[0];
                if (fileName != null && !fileName.isEmpty()) {
                    this.receiveProjectInput.setText(fileName);
                    this.receiveProjectInputDialog.getWindow().setSoftInputMode(4);
                    this.receiveProjectInputDialog.show();
                }
            }
        }
    }

    public File handleFileUri(Uri beamUri) {
        return new File(beamUri.getPath());
    }

    public File handleContentUri(Uri beamUri) {
        if (!TextUtils.equals(beamUri.getAuthority(), "media")) {
            return null;
        }
        Cursor pathCursor = getContentResolver().query(beamUri, new String[]{"_data"}, null, null, null);
        if (pathCursor == null || !pathCursor.moveToFirst()) {
            return null;
        }
        return new File(pathCursor.getString(pathCursor.getColumnIndex("_data")));
    }
}
