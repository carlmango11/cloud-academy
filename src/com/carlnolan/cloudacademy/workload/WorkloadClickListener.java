package com.carlnolan.cloudacademy.workload;

import com.carlnolan.cloudacademy.R;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class WorkloadClickListener implements OnItemClickListener {
	private static int DROP_DOWN_TIME = 100;
	private Activity activity;
	
	public WorkloadClickListener(Activity a) {
		activity = a;
	}

	public void onItemClick(AdapterView<?> list, View view, int position, long id) {
		//final WorkloadListAdapterEntry item = (WorkloadListAdapterEntry) list.getAdapter().getItem(position);
		
	    // set dropdown data
		WorkloadListAdapter.WorkloadListItemHolder holder = (WorkloadListAdapter.WorkloadListItemHolder) view.getTag();
	    final View dropDown = holder.dropdown;

	    // set click close on top part of view, this is so you can click the view
	    // and it can close or whatever, if you start to add buttons etc. you'll loose
	    // the ability to click the view until you set the dropdown view to gone again.
	    final View simpleView = view.findViewById(R.id.workload_list_row_simple_parent_invis_cover);
	    simpleView.setVisibility(View.VISIBLE);

	    final Handler handler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            if(msg.what == 0) {
	                // first we measure height, we need to do this because we used wrap_content
	                // if we use a fixed height we could just pass that in px.
	                ExpandCollapseAnimation.setHeightForWrapContent(activity, dropDown);
	                ExpandCollapseAnimation expandAni = new ExpandCollapseAnimation(dropDown, DROP_DOWN_TIME);
	                dropDown.startAnimation(expandAni);

	                Message newMsg = new Message();

	            } else if(msg.what == 1) {
	                ExpandCollapseAnimation expandAni = new ExpandCollapseAnimation(dropDown, DROP_DOWN_TIME);
	                dropDown.startAnimation(expandAni);

	                simpleView.setOnClickListener(null);
	                simpleView.setVisibility(View.GONE);
	            }
	        }
	    };

	    simpleView.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	            handler.sendEmptyMessage(1);
	        }
	    });

	    // start drop down animation
	    handler.sendEmptyMessage(0);
	}
}
