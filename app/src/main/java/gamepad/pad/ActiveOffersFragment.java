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



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActiveOffersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActiveOffersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveOffersFragment extends Fragment {

    private RecyclerView _recyclerView;
    private RecyclerView.LayoutManager _layoutManager;
    private OffersListAdapter _adapter;
    private long _userID;

    private ActiveOffersFragment.OnFragmentInteractionListener mListener;

    public ActiveOffersFragment() {
    }


    public static ActiveOffersFragment newInstance(long userId) {
        ActiveOffersFragment fragment = new ActiveOffersFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_active_offers, container, false);

        _recyclerView = (RecyclerView) v.findViewById(R.id.actives_itemList);
        _recyclerView.setHasFixedSize(true);

        _layoutManager = new LinearLayoutManager(v.getContext());
        _recyclerView .setLayoutManager(_layoutManager);

        _adapter = new OffersListAdapter(v.getContext(), _userID);
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
        if (context instanceof ActiveOffersFragment.OnFragmentInteractionListener) {
            mListener = (ActiveOffersFragment.OnFragmentInteractionListener) context;
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


class OffersListAdapter extends  RecyclerView.Adapter<OffersListAdapter.ActivesViewHolder> {

    private Context _context;
    private GameListing [] _itemList;
    private long _userID;

    public static  class ActivesViewHolder extends RecyclerView.ViewHolder {
        public TextView _head;
        public TextView _body;

        public ActivesViewHolder (View v) {
            super(v);
            _head = (TextView)v.findViewById(R.id.list_item_Head);
            _body = (TextView)v.findViewById(R.id.list_item_Body);
        }

    }

    public OffersListAdapter(Context context, long userID) {
        _userID = userID;
        _context = context;


    }

    public  void update () {

        _itemList = DBConnection.db(_context).getGameListingsFromUser(_userID);

    }

    @Override
    public OffersListAdapter.ActivesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new ActivesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OffersListAdapter.ActivesViewHolder holder, int position) {

        holder._head.setText(_itemList[position].getName());
        /*long diff = _dateList[position].getTime() - System.currentTimeMillis();
        long daysLeft = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);*/
        holder._body.setText(_itemList[position].getPrice() + "€ por día");
    }

    @Override
    public int getItemCount() {
        if (_itemList == null)
            return 0;

        return _itemList.length;
    }
}

