package amritansh.tripathi.com.searchflix.presentation.movieList

import amritansh.tripathi.com.searchflix.R
import amritansh.tripathi.com.searchflix.network.Movie
import amritansh.tripathi.com.searchflix.presentation.movieDetails.MovieDetailsFragment
import amritansh.tripathi.com.searchflix.utils.ViewModelFactory
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by amritanshtripathi on 3/5/18.
 */

class MovieListFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MovieListViewModel
    private val disposable = CompositeDisposable()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieListAdapter
    private var query: String? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_common, container, false)
        setUpRecyclerView(view)
        query = arguments?.get(QUERY_TAG) as? String
        disposable.add(
                adapter.getClickObservable().subscribe(this::showMovieDetailFragment))
        return view

    }

    override fun onStart() {
        super.onStart()
        if (query == null) {
            disposable.add(viewModel.getMovieList()
                    .subscribe(this::onSuccess, this::onError))
        } else {
            disposable.add(viewModel.searchMovie(query)
                    .subscribe(this::onSuccess, this::onError))
        }

    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    private fun onSuccess(movieList: List<Movie>) {
        adapter.setData(movieList)
    }

    private fun onError(error: Throwable) {
        Log.e(TAG, "Unable to get movieList", error)
    }

    private fun setUpRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.my_recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter = MovieListAdapter(ArrayList<Movie>(0))
        recyclerView.adapter = adapter
    }

    private fun showMovieDetailFragment(movie: Movie) {
        //TODO:Add to fragment Navigator
        val fragmentManager = activity?.supportFragmentManager
        fragmentManager?.beginTransaction()
                ?.replace(R.id.contentFrame, MovieDetailsFragment.newInstance(movie), MovieDetailsFragment::class.java.name)
                ?.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                ?.addToBackStack(this::class.java.name)
                ?.commit()

    }

    companion object {
        @JvmStatic
        fun newInstance(): MovieListFragment {
            return MovieListFragment()
        }

        @JvmStatic
        fun newInstance(searchQuery: String): MovieListFragment {
            val args = Bundle()
            val fragment = MovieListFragment()
            args.putString(QUERY_TAG, searchQuery)
            fragment.arguments = args
            return fragment
        }

        private val QUERY_TAG = " QUERYTAG"
        private val TAG = MovieListFragment::class.java.simpleName

    }


}