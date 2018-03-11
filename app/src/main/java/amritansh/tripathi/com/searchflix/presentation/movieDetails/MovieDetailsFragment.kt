package amritansh.tripathi.com.searchflix.presentation.movieDetails

import amritansh.tripathi.com.searchflix.R
import amritansh.tripathi.com.searchflix.network.Item
import amritansh.tripathi.com.searchflix.network.Movie
import amritansh.tripathi.com.searchflix.presentation.movieList.MovieListFragment
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
class MovieDetailsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var adapter:MovieDetailsAdapter
    private lateinit var recyclerView: RecyclerView
    private var disposable = CompositeDisposable()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieDetailsViewModel::class.java)
        viewModel.movie = arguments?.get(MOVIE_OBJECT) as Movie
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_common, container, false)
        setUpRecyclerView(view)
        disposable.add(
                adapter.getClickObservable().subscribe(this::showMovieDetailFragment))
        return view
    }

    private fun setUpRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.my_recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter = MovieDetailsAdapter(emptyList())
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        disposable.add(viewModel.getMovieDetailsData()
                    .subscribe(this::onSuccess, this::onError))
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    private fun onSuccess(items:List<Item>){
        adapter.setItems(items)
    }

    private fun onError(error: Throwable) {
        Log.e(MovieDetailsFragment.TAG, "Unable to get items", error)
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
        fun newInstance(movie: Movie): MovieDetailsFragment {
            val args = Bundle()
            val fragment = MovieDetailsFragment()
            args.putParcelable(MOVIE_OBJECT, movie)
            fragment.arguments = args
            return fragment
        }

        private val MOVIE_OBJECT = "MOVIEOBJECT"
        private val TAG = this::class.java.simpleName

    }
}