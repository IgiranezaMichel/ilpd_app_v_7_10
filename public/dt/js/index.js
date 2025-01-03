// $(document).ready(function() {
//     $('#example').DataTable( {
//         dom: 'Bfrtip',
//         buttons: [
//             'columnsToggle',
//             'copy',
//             'csv',
//             'excel',
//             'pdf',
//             {
//                 extend: 'print',
//                 text: 'Print all (not just selected)',
//                 exportOptions: {
//                     modifier: {
//                         selected: null
//                     }
//                 }
//             }
//         ],
//         select:true
//     } );
// } );


$(document).ready(function () {
    var table = $('#example').DataTable({
        lengthChange: true,
        buttons: [{
            extend: 'copy',
            exportOptions: {
                columns: ':visible'
            }
        }, {
            extend: 'csv',
            exportOptions: {
                columns: ':visible'
            }
        }, {
            extend: 'print',
            exportOptions: {
                columns: ':visible'
            }
        }, {
            extend: 'excel',
            exportOptions: {
                columns: ':visible'
            }
        }, {
            extend: 'pdf',
            exportOptions: {
                columns: ':visible'
            }
        }, 'colvis'
        ], columnDefs: [{
            targets: -1,
            visible: false
        }]
    });

    table.buttons().container()
        .appendTo('#example_wrapper .col-md-6:eq(0)');
});