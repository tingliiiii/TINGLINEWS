// 表單提交事件處理
const handleFormSubmit = async (event) => {

	event.preventDefault(); // 停止表單的預設傳送行為，改成自訂行為

	const formData = {
		userName: $('#userName').val(),
		gender: $('input[name="gender"]:checked').val(),
		birth: $('#birth').val(),
		phone: $('#phone').val(),
		userEmail: $('#userEmail').val(),
		userPassword: $('#userPassword').val()
	};

	await addUser(formData);
};

const addUser = async (formData) => {
	try {
		const response = await fetch('http://localhost:8080/tinglinews/user/register', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(formData),
            credentials: 'include' // 確保请求包含 cookies
		});

		const { state, message, data } = await response.json();
		console.log(message);

		// 根據註冊狀態進行跳轉
		if (state === true && data && data.userId) {
			// 將 userId 儲存至 sessionStorage
			sessionStorage.setItem('userId', data.userId);
			window.location.href = '/tinglinews/user/profile.html';
		} else {
			alert('註冊失敗' + message);
		}
	} catch (error) {
		console.error('註冊請求錯誤：', error);
		alert('註冊過程中出現錯誤，請稍後再試');
	}

};

$(document).ready(() => {

	$('.header-container').load('../nav.html');
	$('.footer-container').load('../footer.html');

	// 綁定表單提交事件
	$('#register-form').on('submit', handleFormSubmit);
});


// 延伸功能
// 註冊時跳錯誤：email 不可重複註冊