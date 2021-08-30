package com.hsproject.proximity.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hsproject.proximity.R;
import com.hsproject.proximity.constants.Category;

import java.util.ArrayList;
import java.util.Arrays;

public class FragmentPage2 extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_page_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // spinners
        Spinner spiJoinedRoomSort = getView().findViewById(R.id.spiJoinedRoomSort);
        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add("최근 참여한 순");
        categoryList.add("가까운 거리 순");

        ArrayAdapter<String> spiJoinedRoomSortArrayAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, categoryList);
        spiJoinedRoomSort.setAdapter(spiJoinedRoomSortArrayAdapter);

        spiJoinedRoomSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).setJoinedRoomSortBy(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}