package org.hcilab.projects.nlogx.ui;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.hcilab.projects.nlogx.R;

public class BrowseActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

	private RecyclerView recyclerView;
	private SwipeRefreshLayout swipeRefreshLayout;
	BrowseAdapter adapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView = findViewById(R.id.list);
		recyclerView.setLayoutManager(layoutManager);

		swipeRefreshLayout = findViewById(R.id.swiper);
		swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
		swipeRefreshLayout.setOnRefreshListener(this);

		update();

		new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

				if(direction == ItemTouchHelper.LEFT) {
					adapter.deleteItem(((BrowseViewHolder) viewHolder).getAdapterPosition());
				}

			}
			@Override
			public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
				if (viewHolder != null) {
					final View foregroundView = ((BrowseViewHolder) viewHolder).viewForeground;
					getDefaultUIUtil().onSelected(foregroundView);
				}
			}

			@Override
			public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
				final View foregroundView = ((BrowseViewHolder) viewHolder).viewForeground;
				getDefaultUIUtil().clearView(foregroundView);
			}

			@Override
			public void onChildDraw(Canvas c, RecyclerView recyclerView,
									RecyclerView.ViewHolder viewHolder, float dX, float dY,
									int actionState, boolean isCurrentlyActive) {
				final View foregroundView = ((BrowseViewHolder) viewHolder).viewForeground;

				getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
						actionState, isCurrentlyActive);

			}

			@Override
			public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
										RecyclerView.ViewHolder viewHolder, float dX, float dY,
										int actionState, boolean isCurrentlyActive) {
				final View foregroundView = ((BrowseViewHolder) viewHolder).viewForeground;
				getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
						actionState, isCurrentlyActive);
				if(getSwipeDirs(recyclerView,viewHolder) == ItemTouchHelper.RIGHT){
					((BrowseViewHolder) viewHolder).viewBackground_delete.setBackgroundColor(getResources().getColor(R.color.green));
					((BrowseViewHolder) viewHolder).option_text.setText("ADD TO FAVORITES");
				}
			}


		}).attachToRecyclerView(recyclerView);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && DetailsActivity.ACTION_REFRESH.equals(data.getStringExtra(DetailsActivity.EXTRA_ACTION))) {
			update();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.browse, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_refresh:
				update();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void update() {
		adapter = new BrowseAdapter(this, false);
		recyclerView.setAdapter(adapter);

		if(adapter.getItemCount() == 0) {
			Toast.makeText(this, R.string.empty_log_file, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public void onRefresh() {
		update();
		swipeRefreshLayout.setRefreshing(false);
	}
}