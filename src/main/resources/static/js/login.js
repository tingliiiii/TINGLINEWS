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
			body: JSON.stringify(formData),
            credentials: 'include' // 確保请求包含 cookies
		});

		const { state, message, data } = await response.json();
		console.log(message);
		console.log(data);

		// 根據註冊狀態進行跳轉
		if (state === true && data && data.userId) {
			// 將 userId 儲存至 sessionStorage
			sessionStorage.setItem('userId', data.userId);
			window.location.href = '/tinglinews/user/profile.html';
		} else {
			alert('登入失敗' + message);
		}
	} catch (error) {
		console.error('登入錯誤：', error);
		alert('登入過程中出現錯誤，請稍後再試');
	}

};


$(document).ready(() => {

	$('.header-container').load('../nav.html');
	$('.footer-container').load('../footer.html');
	// 綁定表單提交事件
	$('#login-form').on('submit', handleFormSubmit);

});