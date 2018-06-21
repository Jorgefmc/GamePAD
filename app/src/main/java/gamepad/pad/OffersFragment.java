package gamepad.pad;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class OffersFragment extends Fragment {

    private RecyclerView _recyclerView;
    private LayoutManager _layoutManager;
    private OffersItemListAdapter _adapter;
    private long _userID;
    private OnFragmentInteractionListener mListener;

    public OffersFragment() {
    }

    public static OffersFragment newInstance(long userId) {
        OffersFragment fragment = new OffersFragment();
        Bundle args = new Bundle();
        args.putLong("id", userId);
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
        View v = inflater.inflate(R.layout.fragment_offers, container, false);


        _recyclerView = (RecyclerView) v.findViewById(R.id.offers_itemsList);
        _recyclerView.setHasFixedSize(true);

        _layoutManager = new LinearLayoutManager(v.getContext());
        _recyclerView .setLayoutManager(_layoutManager);

        _userID = getArguments().getLong("id");
        _adapter = new OffersItemListAdapter(v.getContext(), _userID);
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

class OffersItemListAdapter extends  RecyclerView.Adapter<OffersItemListAdapter.OffersViewHolder> {
    private Context _context;
    private long _userID;
    private GameListing [] _gameList;


    public static  class OffersViewHolder extends RecyclerView.ViewHolder {
        public TextView _head;
        public TextView _body;

        public OffersViewHolder(View v) {
            super(v);
            _head = (TextView)v.findViewById(R.id.list_item_Head);
            _body = (TextView)v.findViewById(R.id.list_item_Body);
        }

    }

    public OffersItemListAdapter(Context context, long userID) {
        _context = context;
        _userID = userID;
    }

    public void update() {
        _gameList = DBConnection.db(_context).getExclusiveGameListings(_userID);
    }

    @Override
    public OffersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);


        return new OffersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OffersViewHolder holder, int position) {
        if (getItemCount() > 0) {
            holder._head.setText(_gameList[position].getName());
            holder._body.setText(_gameList[position].getPrice() + "€ por día");
        }
    }
    public int getItemCount() {
        if (_gameList == null)
            return 0;
        return _gameList.length;
    }



    private class OffersLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
