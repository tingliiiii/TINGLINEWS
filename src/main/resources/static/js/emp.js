$(document).ready(() => {

    $('#news-table').DataTable({
        // 需要不需要有下載功能
        // dom: 'Bfrtip',
        //     buttons: [
        //         'copy', 'csv', 'excel', 'pdf', 'print'
        //     ],
        scrollX: true,
        scrollY: true
        // columns: [null, null, { width: '20%' }, null, null, null, null, null]
    }
    );

});