package com.adentweets.app.domain.model

data class Poll(
    val pollId: String = "",
    val options: List<PollOption> = emptyList(),
    val durationHours: Int = 24,
    val createdAt: Long = System.currentTimeMillis(),
    val endsAt: Long = 0L,
    val totalVotes: Long = 0,
    val votedBy: Map<String, Int> = emptyMap() // uid -> optionIndex
) {
    val isExpired: Boolean get() = System.currentTimeMillis() > endsAt

    fun getVotedOptionIndex(uid: String): Int? = votedBy[uid]

    fun hasUserVoted(uid: String): Boolean = votedBy.containsKey(uid)

    companion object {
        fun create(options: List<String>, durationHours: Int): Poll {
            val pollOptions = options.mapIndexed { index, text ->
                PollOption(index = index, text = text, voteCount = 0)
            }
            val endsAt = System.currentTimeMillis() + (durationHours * 60 * 60 * 1000L)
            return Poll(
                pollId = java.util.UUID.randomUUID().toString(),
                options = pollOptions,
                durationHours = durationHours,
                endsAt = endsAt
            )
        }
    }
}

data class PollOption(
    val index: Int = 0,
    val text: String = "",
    val voteCount: Long = 0
) {
    fun percentage(totalVotes: Long): Float =
        if (totalVotes > 0) (voteCount.toFloat() / totalVotes) * 100f else 0f
}