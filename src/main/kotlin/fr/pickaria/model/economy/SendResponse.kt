package fr.pickaria.model.economy

enum class SendResponse {
	RECEIVE_ERROR,
	REFUND_ERROR,
	SUCCESS,
	SEND_ERROR,
	NOT_ENOUGH_MONEY;
}