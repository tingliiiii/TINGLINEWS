// 從後端抓資料給 profile
const fetchData = async (userId) => {
	try {
		const response = await fetch(`http://localhost:8080/tinglinews/user/register/${userId}`, {
            method: 'GET',
            credentials: 'include' // 确保请求包含 cookies
        });
		const { state, message, data } = await response.json(); // 等待回應本文內容
		console.log(state, message, data);
		
		$('#userId').val(data.userId);
		$('#userEmail').val(data.userEmail);
		$('#userName').val(data.userName);
		$('#gender').val(data.gender);
		$('#birth').val(data.birth);
		$('#phone').val(data.phone);
		
	} catch (e) {
		console.error(e);
	}
}

$(document).ready(async () => {

	$('.header-container').load('../nav-login.html');
	$('.footer-container').load('../footer.html');
	$('.ad-container').load('../ad.html');

	const userId = sessionStorage.getItem('userId');
	console.log('用戶 ID：', userId);
	if (!userId) {
		console.log('用戶 ID 未找到');
		alert('請重新登入');
		window.location.href = '/login';
		return;
	}
	fetchData(userId);


});