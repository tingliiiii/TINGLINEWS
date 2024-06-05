// 表單提交事件處理
const handleSubmit = async (event) => {

	event.preventDefault(); // 停止表單的預設傳送行為，改成自訂行為

	const formData = {
		userEmail: $('#userEmail').val(),
		userPassword: $('#userPassword').val()
	};

	await login(formData);
};

const login = async (formData) => {
	console.log(formData);
	try {
		const response = await fetch('http://localhost:8080/tinglinews/emp/login', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(formData)
		});

		const { state, message, data } = await response.json();
		console.log(state, message, data);

		// 根據註冊狀態進行跳轉
		if (state == true) {
			// 將 userId 儲存至 sessionStorage（判斷用戶是否已登入）
			sessionStorage.setItem('userId', data.userId);
			sessionStorage.setItem('userName', data.userName);
			sessionStorage.setItem('authrotyName', data.authority.authorityName);
		
			Swal.fire(message, '', 'success');
			setTimeout(() => {
				window.location.replace('/tinglinews/emp/content-management.html');
			}, 1000);
			return;

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

	$('#emp-login-form').on('submit', handleSubmit);

});