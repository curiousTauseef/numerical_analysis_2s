/*
 * Copyright (c) 2018. Evren Coşkun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.example.sacrew.numericov4.fragments.tableview.holder;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder;
import com.evrencoskun.tableview.sort.SortState;
import com.example.sacrew.numericov4.R;
import com.example.sacrew.numericov4.fragments.tableview.model.ColumnHeader;

/**
 * Created by evrencoskun on 23/10/2017.
 */

public class ColumnHeaderViewHolder extends AbstractSorterViewHolder {

    private static final String LOG_TAG = ColumnHeaderViewHolder.class.getSimpleName();

    private final LinearLayout column_header_container;
    private final TextView column_header_textview;
    private final ImageButton column_header_sortButton;

    private final Drawable arrow_up, arrow_down;

    public ColumnHeaderViewHolder(View itemView, ITableView tableView) {
        super(itemView);
        column_header_textview = itemView.findViewById(R.id.column_header_textView);
        column_header_container = itemView.findViewById(R.id
                .column_header_container);
        column_header_sortButton = itemView.findViewById(R.id
                .column_header_sortButton);

        // initialize drawables
        arrow_up = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_up);
        arrow_down = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_down);

        // Set click listener to the sort button
        View.OnClickListener mSortButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (getSortState()) {
                    case ASCENDING:
                        tableView.sortColumn(getAdapterPosition(), SortState.DESCENDING);
                        break;
                    case DESCENDING:
                        tableView.sortColumn(getAdapterPosition(), SortState.ASCENDING);
                        break;
                    default:
                        // Default one
                        tableView.sortColumn(getAdapterPosition(), SortState.DESCENDING);
                        break;
                }

            }
        };
        column_header_sortButton.setOnClickListener(mSortButtonClickListener);
    }


    /**
     * This method is calling from onBindColumnHeaderHolder on TableViewAdapter
     */
    public void setColumnHeader(ColumnHeader columnHeader) {
        column_header_textview.setText(String.valueOf(columnHeader.getData()));

        // If your TableView should have auto resize for cells & columns.
        // Then you should consider the below lines. Otherwise, you can remove them.

        // It is necessary to remeasure itself.
        column_header_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        column_header_textview.requestLayout();
    }

    @Override
    public void onSortingStatusChanged(SortState sortState) {
        Log.e(LOG_TAG, " + onSortingStatusChanged : x:  " + getAdapterPosition() + " old state "
                + getSortState() + " current state : " + sortState + " visiblity: " +
                column_header_sortButton.getVisibility());

        super.onSortingStatusChanged(sortState);

        // It is necessary to remeasure itself.
        column_header_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;

        controlSortState(sortState);

        Log.e(LOG_TAG, " - onSortingStatusChanged : x:  " + getAdapterPosition() + " old state "
                + getSortState() + " current state : " + sortState + " visiblity: " +
                column_header_sortButton.getVisibility());

        column_header_textview.requestLayout();
        column_header_sortButton.requestLayout();
        column_header_container.requestLayout();
        itemView.requestLayout();
    }

    private void controlSortState(SortState sortState) {
        switch (sortState) {
            case ASCENDING:
                column_header_sortButton.setVisibility(View.VISIBLE);
                column_header_sortButton.setImageDrawable(arrow_down);

                break;
            case DESCENDING:
                column_header_sortButton.setVisibility(View.VISIBLE);
                column_header_sortButton.setImageDrawable(arrow_up);
                break;
            default:
                column_header_sortButton.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
