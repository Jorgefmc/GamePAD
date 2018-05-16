package gamepad.pad;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private String [] _items = {"God of War", "Nier: Automata", "Transistor", "Assassins Creed"};

    private RecyclerView _recyclerView;
    private RecyclerView.Adapter _adapter;
    private RecyclerView.LayoutManager _layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       _recyclerView = (RecyclerView) findViewById(R.id.itemsList);
       _recyclerView.setHasFixedSize(true);

       _layoutManager = new LinearLayoutManager(this);
       _recyclerView .setLayoutManager(_layoutManager);

       _adapter = new ItemListAdapter (_items, this);
       _recyclerView.setAdapter(_adapter);

    }
}

class ItemListAdapter extends  RecyclerView.Adapter<ItemListAdapter.ViewHolder> {
    private String [] _itemList;
    private Context _context;

    public static  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView _head;
        public TextView _body;

        public ViewHolder (View v) {
            super(v);
            _head = (TextView)v.findViewById(R.id.list_item_Head);
            _body = (TextView)v.findViewById(R.id.list_item_Body);
        }

    }

    public ItemListAdapter (String [] itemList, Context context) {
        _itemList = itemList;
        _context = context;

    }

    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListAdapter.ViewHolder holder, int position) {
        holder._head.setText(_itemList[position]);
    }

    @Override
    public int getItemCount() {
        return _itemList.length;
    }
}
