package com.example.myapplication3.fragments.DisplayFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.example.myapplication3.R;
import com.topjohnwu.superuser.Shell;

import java.util.ArrayList;
import java.util.List;

public class DisplayFragment extends Fragment {
    private static final String TAG = "HomeActivity";
    Button button1, button2, button3;
    ArrayList<DisplayList> displayArrayLists;
    final String REF_HIGH = "120", REF_LOW = "60.0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display, container, false);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button1 = view.findViewById(R.id.buttonrerrate1);
        button2 = view.findViewById(R.id.buttonrerrate2);
        button3 = view.findViewById(R.id.buttonrerrate3);

        button1.setOnClickListener(v -> {
            Shell.cmd("settings put system peak_refresh_rate 120").exec();
            Shell.cmd("settings put system min_refresh_rate 120").exec();
            Toast.makeText(getActivity(),
                    "Set 120Hz",
                    Toast.LENGTH_SHORT).show();

        });
        button2.setOnClickListener(v -> {
            Shell.cmd("settings put system peak_refresh_rate 60").exec();
            Shell.cmd("settings put system min_refresh_rate 60").exec();
            Toast.makeText(getActivity(),
                    "Set 60Hz",
                    Toast.LENGTH_SHORT).show();
        });
        button3.setOnClickListener(v -> {
            Shell.cmd("settings put system peak_refresh_rate 120").exec();
            Shell.cmd("settings put system min_refresh_rate 60").exec();
            Toast.makeText(getActivity(),
                    "Set to Auto",
                    Toast.LENGTH_SHORT).show();
        });
        initList();
        initRecyclerView(view);
        setSingleSwitch(view);
    }
    public void setSingleSwitch(View view){
        Shell.Result res;
        List<String> out1, out2;
        int SLval = 5, SMLval = 1;
        String SL = "/proc/touchpanel/sensitive_level";
        String SML = "/proc/touchpanel/smooth_level";
        Switch singleSwitch = view.findViewById(R.id.singleSwitch);
        res = Shell.cmd("cat "+SL).exec();
        out1 = res.getOut();
        res = Shell.cmd("cat "+SML).exec();
        out2 = res.getOut();
        if((Integer.parseInt(out1.get(0)) == SLval) &&
                (Integer.parseInt(out2.get(0)) == SMLval)){
            singleSwitch.setChecked(true);
        }
        singleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked == true) {
                Shell.cmd("echo 5 > " + SL).exec();
                Shell.cmd("echo 1 > " + SML).exec();
            } else {
                Shell.cmd("echo 0 > " + SL).exec();
                Shell.cmd("echo 0 > " + SML).exec();
            }
        });
    }
    public void initList(){
        String[] PrimaryDesc = {"DC Dimming","High Brightness Mode","Double-Tap to Wake"};
        String[] SecondaryDesc = {"Stops screen flicker", "Pushes Display to max brightness level",
                        "Double tap on screen wakes up device"};
        String DCDPath = "/sys/kernel/oppo_display/dimlayer_bl_en";
        String HBMPath = "/sys/kernel/oppo_display/hbm";
        String D2W = "/proc/sys/kernel/slide_boost_enabled";
        String[] FilePaths = {DCDPath, HBMPath, D2W};
        int[] ActionSet = {1, 1, 1};
        int[] ActionUnset = {0, 0, 0};
        displayArrayLists = new ArrayList<>();
        for(int i = 0; i < PrimaryDesc.length; i++){
            DisplayList displayList = new DisplayList(PrimaryDesc[i], SecondaryDesc[i],
                    FilePaths[i], ActionSet[i], ActionUnset[i]);
            displayArrayLists.add(displayList);
        }
    }
    public void initRecyclerView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.display_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView. setOverScrollMode(View. OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(displayArrayLists);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}