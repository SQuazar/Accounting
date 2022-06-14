const Controller = {

    products: {},


    init: function () {
        Controller.loadProducts(function () {
            Controller.getInvoices();
        });
    },

    getInvoices: function () {
        let date = $('#invoiceDateId').val();
        if (date === '')
            date = new Date(0);
        let sortType = $('#invoiceSortSelect').val();
        if (typeof sortType != 'string')
            sortType = 'none';
        const sortInverse = $('#inverseSort').is(':checked');
        $.ajax({
            type: 'POST',
            url: `/api/invoice/all?sort=${sortType}&inverse=${sortInverse}&date=${new Date(date).getTime()}`,
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function (data) {
                Controller.fillTable(data);
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    fillTable: function (data) {
        const tbody = $('#invoicesTableBody')[0];
        let rows = ``;
        for (let i = 0; i < data.length; i++) {
            rows +=
                `
                    <tr>
                        <td>${data[i]['id']}</td>
                        <td><a href="javascript:void(0)" onclick="Controller.showProductModal(${data[i]['product']})">${Controller.getProduct(data[i]['product'])['product_name']}</a></td>
                        <td>${data[i]['count']}</td>
                        <td>${data[i]['invoice_type']['localed']}</td>
                        <td>${data[i]['person']}</td>
                        <td>${data[i]['date']}</td>
                    </tr>
                `
        }
        tbody.innerHTML = rows;
    },

    getProduct: function (id) {
        return Controller.products[id.toString()];
    },

    loadProducts: function (callback) {
        const selection = $('#productIdSelection')[0];
        $.ajax({
            type: 'POST',
            url: '/api/stock/all',
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function (data) {
                let productsOption = `<option disabled selected value="none">Выберите товар</option>`;
                for (let i = 0; i < data.length; i++) {
                    Controller.products[data[i]['product_id'].toString()] = data[i];
                    productsOption += `<option value="${data[i]['product_id']}">${data[i]['product_id']} | ${data[i]['product_name']}</option>`;
                }
                selection.innerHTML = productsOption;
                callback();
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    createInvoice: function () {
        let productId;
        if (isNaN(productId = Number.parseInt($('#productIdSelection').val()))) return;
        let count;
        if (isNaN(count = Number.parseInt($('#productCount').val()))) return;
        const person = $('#invoicePerson').val();
        if (person === '') return;
        const invoiceType = $('#invoiceTypeSelect').val();
        if (invoiceType === '') return;
        const date = new Date($('#invoiceDate').val());
        const invoice_date = parseToValidDate(date);
        const invoice = {
            'product': productId,
            'invoice_type': invoiceType,
            'count': count,
            'person': person,
            'date': invoice_date
        };
        $.ajax({
            type: 'POST',
            url: '/api/invoice/add',
            contentType: 'application/json',
            data: JSON.stringify(invoice),
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function () {
                $('#productIdSelection').val('');
                $('#productCount').val('');
                $('#invoicePerson').val('');
                $('#invoiceTypeSelect').val('');
                $('#invoiceDate').val('');
                const remainderElement = $('#productRemainder', true);
                remainderElement.attr('hidden');
                Controller.init();
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    showProductModal: function (id) {
        const modal = new bootstrap.Modal($('#productModal')[0]);
        const product = Controller.getProduct(id);
        $('#modalProductId').text(product['product_id']);
        $('#modalProductName').text(product['product_name']);
        $('#modalProductCount').text(product['count']);
        $('#modalProductPrice').text(product['price']);
        $('#modalProductDate').text(product['delivery_date']);
        modal.show();
    }
}