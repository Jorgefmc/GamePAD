package gamepad.pad;


import android.app.Fragment;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;


public class HistoryFragment extends Fragment {
    private RecyclerView _recyclerView;
    private RecyclerView.LayoutManager _layoutManager;
    private HistoryListAdapter _adapter;
    private long _userID;

    private OnFragmentInteractionListener mListener;

    public HistoryFragment() {
        // Required empty public constructor
    }


    public static HistoryFragment newInstance(long userId) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putLong("user", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _userID = getArguments().getLong("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        _recyclerView = (RecyclerView) v.findViewById(R.id.history_list);
        _recyclerView.setHasFixedSize(true);

        _layoutManager = new LinearLayoutManager(v.getContext());
        _recyclerView .setLayoutManager(_layoutManager);

        _adapter = new HistoryListAdapter(v.getContext(), _userID);
        _recyclerView.setAdapter(_adapter);

        return v;
    }

    @Override
    public void onResume () {
        super.onResume();
        _adapter.update();
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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


class HistoryListAdapter extends  RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder> {

    private Context _context;
    private GameRenting [] _itemList;
    private long _userID;
    private DecimalFormat _df;
    private SimpleDateFormat _dateFormat;

    public static  class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView _head;
        public TextView _body;
        public ImageView _status;

        public HistoryViewHolder (View v) {
            super(v);
            _head = (TextView)v.findViewById(R.id.history_list_item_Head);
            _body = (TextView)v.findViewById(R.id.history_list_item_Body);
            _status = (ImageView) v.findViewById(R.id.history_list_item_status);
        }

    }

    public HistoryListAdapter(Context context, long userID) {
        _userID = userID;
        _context = context;
        _df = new DecimalFormat();
        _df.setMaximumFractionDigits(2);
        _dateFormat = new SimpleDateFormat("dd/MM/yy");

    }

    public  void update () {

        _itemList = DBConnection.db(_context).getHistoryByUser(_userID);

    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);

        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HistoryListAdapter.HistoryViewHolder holder, int position) {

        holder._head.setText(_itemList[position].getName());
        float totalAmount =  _itemList[position].getPrice();
        String startDate = _dateFormat.format(_itemList[position].getStartDate());
        String endDate = _dateFormat.format(_itemList[position].getEndDate());
        holder._body.setText(_df.format(totalAmount) + "€ (" + startDate + " - " + endDate + ")");
        if (_userID == _itemList[position].getGiver())
            holder._status.setImageResource(R.drawable.ic_given);
    }

    @Override
    public int getItemCount() {
        if (_itemList == null)
            return 0;

        return _itemList.length;
    }
}
