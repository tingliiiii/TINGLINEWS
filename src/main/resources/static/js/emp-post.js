// const ip = '127.0.0.1';
const ip = 'localhost';
// 從後端抓資料
const fetchData = async (uri) => {
	const url = `http://localhost:8080/tinglinews${uri}`;
	try {
		const response = await fetch(url); // 等待 fetch 請求完成
		const { state, message, data } = await response.json(); // 等待回應本文內容
		// console.log(state, message, data);
		return data;
	} catch (e) {
		console.error(e);
		return [];
	}
};

// 新增文章的標籤選項
const loadTags = async () => {
	try {
		const data = await fetchData('/emp/tags');
		const select = $('#tags');

		data.slice(1).forEach(tag => {
			const option = $('<option></option>');
			option.attr('value', tag.tagId);
			option.text(tag.tagName);
			select.append(option);
		});
	} catch (error) {
		console.error('Fetching data error:', error);
	}

};

const loadJournalistOptions = async () => {
	try {
		const data = await fetchData('/emp/journalists');
		const select = $('#journalistIds');
		data.forEach(journalist => {
			const option = $('<option></option>');
			option.attr('value', journalist.userId);
			option.text(journalist.userName);
			select.append(option);
		});

		$('#journalistIds').select2({
			placeholder: '請選擇記者'
		});
	} catch (error) {
		console.error('Fetching data error:', error);
	}
}

// 表單提交事件處理
const handleSubmit = async (event) => {

	event.preventDefault();

	const formData = {
		title: $('#title').val(),
		tagId: $('#tags').val(),
		content: tinymce.get('content').getContent(),
		userId: JSON.parse(sessionStorage.getItem('userData')).userId,
		image: $('#fileInput').data('base64'), // Base64 字串
		journalistIds: $('#journalistIds').val()
	};

	if ($('#submit-btn').text() === '新增文章') {
		await submitPost(formData);
	} else {
		await updatePost(formData);
	}

};

// 新增文章
const submitPost = async (formData) => {
	try {
		const response = await fetch(`http://${ip}:8080/tinglinews/emp/post`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(formData)
		});

		const { state, message, data } = await response.json();
		// console.log(state, message, data);

		if (state) {
			Swal.fire(message, '', 'success');
			setTimeout(() => {
				window.location.replace('/tinglinews/emp/content-management.html');
			}, 1000);
		} else {
			Swal.fire(message, '', 'warning');
		}
	} catch (error) {
		console.error('新增文章錯誤：', error);
		Swal.fire('新增文章錯誤 請稍後再試', error, 'error');
	}

};

// 修改文章
const updatePost = async (formData) => {
	try {
		const newsId = JSON.parse(sessionStorage.getItem('newsData')).newsId;
		const response = await fetch(`http://${ip}:8080/tinglinews/emp/news/${newsId}`, {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(formData),
			// credentials: 'include' // 確保請求包含 cookies
		});

		const { state, message, data } = await response.json();
		// console.log(state, message, data);

		if (state) {
			Swal.fire(message, '', 'success');
			setTimeout(() => {
				window.location.replace('/tinglinews/emp/content-management.html');
			}, 1000);
		} else {
			Swal.fire(message, '', 'warning');
		}
	} catch (error) {
		console.error('修改文章錯誤：', error);
		Swal.fire('修改文章錯誤 請稍後再試', error, 'error');
	}

};

$(document).ready(() => {

	const userData = JSON.parse(sessionStorage.getItem('userData'));

	if (!userData) {
		window.location.replace('/tinglinews/emp/login.html');
		return;
	}

	loadTags();
	loadJournalistOptions();

	// 內文編輯器
	tinymce.init({
		selector: '#content',
		language: 'zh_TW',
		content_css: `http://${ip}:8080/tinglinews/css/news.css`,
		plugins: 'autolink charmap codesample emoticons image link lists media searchreplace table visualblocks wordcount checklist mediaembed casechange formatpainter pageembed linkchecker a11ychecker powerpaste autocorrect inlinecss markdown',
		toolbar: 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table mergetags | addcomment showcomments | a11ycheck | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat',
		setup: (editor) => {
			editor.on('init', () => {
				// 如果 sessionStorage 中有 data 就是修改文章
				const data = JSON.parse(sessionStorage.getItem('newsData'));
				if (data != null) {
					$('#title').val(data.title);
					$('#tags').val(data.tagId);
					tinymce.get('content').on('init', (event) => {
						event.target.setContent(data.content);
					});
					if (data.image) {
						// 檢查圖片格式並動態設置
						let imageFormat = 'jpeg'; // 默認為 jpeg
						if (data.image.startsWith('/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAoHBwkHBgoICAgLCg8LDhgQDg0NDh0VFhEYITIjJh0pKycyMTI0GyUoKDcwJzgsLCkqLjYxNTU1HyY3Pi0zP')) {
							imageFormat = 'png';
						}
						$('#imgArea').html('<img src="data:image/' + imageFormat + ';base64,' + data.image + '" width="200" class="m-2">');
						$('#fileInput').data('base64', data.image);
					}
					$('#submit-btn').text('修改');
				};
			});
		}
	});

	// 表單提交
	$('#post-form').on('submit', handleSubmit);

	// 上傳圖片
	$('#fileInput').on('change', function () {

		const files = this.files;

		if (files.length > 1) {
			Swal.fire('錯誤', '只能上傳一張照片', 'error');
			this.value = ''; // 清空選擇的文件
			return;
		}

		const file = files[0];
		const maxFileSize = 1024 * 1024; // 1MB
		if (file.size > maxFileSize) {
			Swal.fire('錯誤', '檔案大小不能超過 1MB', 'error');
			this.value = ''; // 清空選擇的文件
			return;
		}

		const reader = new FileReader();
		reader.onload = function (event) {
			const base64String = event.target.result.split(',')[1]; // Base64 字串
			$('#fileInput').data('base64', base64String); // 將 Base64 字串儲存在 fileInput 元素上
			$('#imgArea').html('<img src="' + event.target.result + '" width="200" class="m-2">');
		};
		reader.readAsDataURL(file);
	});



});

