package gamepad.pad;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;


public class ActiveOnRentFragment extends Fragment {
    private RecyclerView _recyclerView;
    private RecyclerView.LayoutManager _layoutManager;
    private OnRentListAdapter _adapter;
    private long _userID;

    private ActiveOnRentFragment.OnFragmentInteractionListener mListener;

    public ActiveOnRentFragment() {
        // Required empty public constructor
    }

    public static ActiveOnRentFragment newInstance(long userId) {
        ActiveOnRentFragment fragment = new ActiveOnRentFragment();
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
        View v = inflater.inflate(R.layout.fragment_active_on_rent, container, false);

        _recyclerView = (RecyclerView) v.findViewById(R.id.active_on_rent_list);
        _recyclerView.setHasFixedSize(true);

        _layoutManager = new LinearLayoutManager(v.getContext());
        _recyclerView .setLayoutManager(_layoutManager);

        _adapter = new OnRentListAdapter(v.getContext(), _userID, this);
        _recyclerView.setAdapter(_adapter);



        return v;
    }

    @Override
    public void onResume () {
        super.onResume();
        _adapter.update();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onRentFinish (GameRenting rent) {

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

class OnRentListAdapter extends  RecyclerView.Adapter<OnRentListAdapter.OnRentViewHolder> {

    private ActiveOnRentFragment _parent;
    private Context _context;
    private GameRenting [] _itemList;
    private long _userID;
    private DecimalFormat _df;

    public static  class OnRentViewHolder extends RecyclerView.ViewHolder {
        public TextView _head;
        public TextView _body;
        public Button _finishButton;

        public OnRentViewHolder (View v) {
            super(v);
            _head = (TextView)v.findViewById(R.id.rent_list_item_Head);
            _body = (TextView)v.findViewById(R.id.rent_list_item_Body);
            _finishButton = (Button) v.findViewById(R.id.rent_finish_rent);
        }

    }

    public OnRentListAdapter(Context context, long userID, ActiveOnRentFragment parent) {
        _userID = userID;
        _context = context;
        _df = new DecimalFormat();
        _df.setMaximumFractionDigits(2);
        _parent = parent;

    }

    public  void update () {

        _itemList = DBConnection.db(_context).getOnRentByUser(_userID);

    }

    @Override
    public OnRentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_list_item, parent, false);

        return new OnRentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OnRentListAdapter.OnRentViewHolder holder, final int position) {

        holder._head.setText(_itemList[position].getName());
        long diff = _itemList[position].getStartDate().getTime() - System.currentTimeMillis();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        float totalAmount = days * _itemList[position].getPrice();
        holder._body.setText(_df.format(totalAmount) + "€");
        holder._finishButton.setText("Recuperar");
        holder._finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _parent.onRentFinish(_itemList[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (_itemList == null)
            return 0;

        return _itemList.length;
    }
}
