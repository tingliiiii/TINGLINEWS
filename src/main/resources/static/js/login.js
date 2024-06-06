// 表單提交事件處理
const handleFormSubmit = async (event) => {

	event.preventDefault(); // 停止表單的預設傳送行為，改成自訂行為

	const formData = {
		userEmail: $('#userEmail').val(),
		userPassword: $('#userPassword').val()
	};

	await login(formData);
};

const login = async (formData) => {
	try {
		const response = await fetch('http://localhost:8080/tinglinews/user/login', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(formData)
		});

		const { state, message, data } = await response.json();
		// console.log(state, message, data);

		// 根據註冊狀態進行跳轉
		if (state === true && data && data.userId) {
			// 將 userId 儲存至 sessionStorage（判斷用戶是否已登入）
			sessionStorage.setItem('userId', data.userId);
			// 顯示於 nav-login #welcome
			sessionStorage.setItem('userName', data.userName);
			
			// 如果沒有 email 跳轉至個人資料頁面
			const email = sessionStorage.getItem('userEmail');
			if (email == null) {
				sessionStorage.setItem('userEmail', data.userEmail);
				Swal.fire(message, '', 'success');
				setTimeout(() => {
					window.location.replace('/tinglinews/user/profile.html');
				}, 1000);
				return;
			}
			// 如果有 email 則跳轉至贊助頁面
			Swal.fire(message, '', 'success');
			setTimeout(() => {
				window.location.replace('/tinglinews/user/donate.html');
			}, 1000);

		} else {
			Swal.fire(message, '', 'warning');
		}
	} catch (e) {
		console.error('登入錯誤：', e);
		Swal.fire('登入錯誤 請稍後再試', e, 'error');
	}

};


$(document).ready(() => {

	$('.header-container').load('../nav.html');
	$('.footer-container').load('../footer.html');

	const email = sessionStorage.getItem('userEmail');
	$('#userEmail').val(email);

	$('#login-form').on('submit', handleFormSubmit);

});