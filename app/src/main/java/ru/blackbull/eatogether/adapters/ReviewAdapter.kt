package ru.blackbull.eatogether.adapters


//class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.MyViewHolder>() {
//
//    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
//
//    private val diffCallback = object : DiffUtil.ItemCallback<Review>() {
//        override fun areItemsTheSame(oldItem: Review , newItem: Review): Boolean {
//            return oldItem.authorName == oldItem.authorName
//        }
//
//        override fun areContentsTheSame(oldItem: Review , newItem: Review): Boolean {
//            return oldItem == oldItem
//        }
//    }
//    private val differ = AsyncListDiffer(this , diffCallback)
//
//    var reviews: List<Review>
//        get() = differ.currentList
//        set(value) = differ.submitList(value)
//
//    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): MyViewHolder {
//        return MyViewHolder(
//            LayoutInflater.from(parent.context).inflate(
//                R.layout.item_review_preview ,
//                parent ,
//                false
//            )
//        )
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder , position: Int) {
//        val review: Review = reviews[position]
//        holder.itemView.apply {
//            tvReviewPreviewName.text = review.authorName
//            tvReviewPreviewRating.text = review.rating.toString()
//            tvReviewPreviewText.text = review.text
//        }
//    }
//
//    override fun getItemCount(): Int = reviews.size
//}