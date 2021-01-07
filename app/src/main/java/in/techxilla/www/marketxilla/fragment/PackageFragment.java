package in.techxilla.www.marketxilla.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.techxilla.www.marketxilla.MainActivity;
import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.adaptor.SmartPlanAdapter;
import in.techxilla.www.marketxilla.model.SmartPlanModel;

public class PackageFragment extends Fragment {

    View rootView;
    Context mContext;
    RecyclerView recyclerSmartPlan;
    RecyclerView.LayoutManager layoutManager;
    SmartPlanAdapter smartPlanAdapter;
    ViewGroup viewGroup;
    private static ArrayList<SmartPlanModel> smartPlanModelArrayList;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_packages, null);

        initUI();
        return rootView;


    }

    private void initUI() {

        mContext = getContext();

        smartPlanModelArrayList = new ArrayList<SmartPlanModel>();

        //Set up Adapter
        recyclerSmartPlan= (RecyclerView)rootView.findViewById(R.id.recyclerSmartPlan);
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);
        recyclerSmartPlan.setLayoutManager(layoutManager);


        smartPlanAdapter = new SmartPlanAdapter(smartPlanModelArrayList, mContext);
        recyclerSmartPlan.setAdapter(smartPlanAdapter);
        displaySmartPlans();
    }



    private void displaySmartPlans() {

        smartPlanModelArrayList.add(new SmartPlanModel("1 Month", getString(R.string.pcakage1_desc),R.drawable.ic_info,R.color.colorPrimary));
        smartPlanModelArrayList.add(new SmartPlanModel("2 Month", getString(R.string.package2_desc),R.drawable.ic_info,R.color.colorPrimary));
        smartPlanModelArrayList.add(new SmartPlanModel("3 Month", getString(R.string.package3_desc),R.drawable.ic_info,R.color.colorPrimary));
    }


}
