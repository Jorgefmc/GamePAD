package gamepad.pad;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActivesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActivesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivesFragment extends Fragment {

    private String [] _activesList = {"Nier: Automata", "The Witcher 3"};
    private Date [] _datesList = {Date.valueOf("2018-06-19"), Date.valueOf("2018-06-28")};


    private RecyclerView _recyclerView;
    private RecyclerView.LayoutManager _layoutManager;
    private ActivesItemListAdapter _adapter;

    private ActivesFragment.OnFragmentInteractionListener mListener;

    public ActivesFragment() {
        // Required empty public constructor
    }

    public static ActivesFragment newInstance() {
        ActivesFragment fragment = new ActivesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_actives, container, false);

        _recyclerView = (RecyclerView) v.findViewById(R.id.actives_itemList);
        _recyclerView.setHasFixedSize(true);

        _layoutManager = new LinearLayoutManager(v.getContext());
        _recyclerView .setLayoutManager(_layoutManager);

        _adapter = new ActivesItemListAdapter(_activesList, _datesList,v.getContext());
        _recyclerView.setAdapter(_adapter);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivesFragment.OnFragmentInteractionListener) {
            mListener = (ActivesFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}


class ActivesItemListAdapter extends  RecyclerView.Adapter<ActivesItemListAdapter.ActivesViewHolder> {
    private String [] _itemList;
    private Date [] _dateList;
    private Context _context;
    private SimpleDateFormat _dateFormat;

    public static  class ActivesViewHolder extends RecyclerView.ViewHolder {
        public TextView _head;
        public TextView _body;

        public ActivesViewHolder (View v) {
            super(v);
            _head = (TextView)v.findViewById(R.id.list_item_Head);
            _body = (TextView)v.findViewById(R.id.list_item_Body);
        }

    }

    public ActivesItemListAdapter(String [] itemList, Date [] dateList, Context context) {
        _itemList = itemList;
        _dateList = dateList;
        _context = context;
    }

    @Override
    public ActivesItemListAdapter.ActivesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);


        return new ActivesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ActivesItemListAdapter.ActivesViewHolder holder, int position) {
        holder._head.setText(_itemList[position]);
        long diff = _dateList[position].getTime() - System.currentTimeMillis();
        long daysLeft = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        holder._body.setText(daysLeft + " días restantes.");
    }

    @Override
    public int getItemCount() {
        return _itemList.length;
    }
}

