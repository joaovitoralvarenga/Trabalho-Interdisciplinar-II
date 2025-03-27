document.getElementById('form-insert').addEventListener('submit', function(event) {
    event.preventDefault();
    const nome = document.getElementById('nome').value;
    const preco = document.getElementById('preco').value;
    const quantidade = document.getElementById('quantidade').value;
    
    

    fetch('http://localhost:4567/produto/insert', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ nome, preco, quantidade })
    })
    .then(response => {
        console.log('Status da resposta:', response.status);
        
        if (response.status === 201 || response.ok) {
            const contentType = response.headers.get('Content-Type');
            if (contentType && contentType.includes('application/json')) {
                return response.json();
            } else {
                return {}; 
            }
        } else {
            throw new Error(`Resposta não OK. Status: ${response.status}`);
        }
    })
    .then(data => {
        alert('Produto inserido com sucesso!');
        console.log(data);
        
        // Atualiza a lista de produtos sem exibir a mensagem de sucesso
        listarProdutos(false);
    })
    .catch(error => {
        console.error('Erro ao inserir produto:', error);
        alert('Erro ao inserir produto. Verifique o console.');
    });
});

// Função para buscar um produto por ID
function buscarProduto() {
    const id = document.getElementById('produto-id').value;

    fetch(`http://localhost:4567/produto/${id}`)
        .then(response => {
            if (response.ok && response.headers.get('Content-Type').includes('application/json')) {
                return response.json();
            } else {
                throw new Error('Resposta não é JSON ou não está OK');
            }
        })
        .then(data => {
            const produtoDiv = document.getElementById('produto-info');
            produtoDiv.innerHTML = `
                <h3>Produto Encontrado</h3>
                <p>ID: ${data.data.id}</p>
                <p>Nome: ${data.data.nome}</p>
                <p>Preço: ${data.data.preco}</p>
                <p>Quantidade: ${data.data.quantidade}</p>
            `;
        })
        .catch(error => {
            console.error('Erro ao buscar produto:', error);
        });
}

// Função para listar todos os produtos
function listarProdutos(exibirMensagem = true) {
    fetch('http://localhost:4567/produto')
        .then(response => {
            if (response.ok && response.headers.get('Content-Type').includes('application/json')) {
                return response.json();
            } else {
                throw new Error('Resposta não é JSON ou não está OK');
            }
        })
        .then(data => {
            const produtosListDiv = document.getElementById('produtos-list');
            produtosListDiv.innerHTML = '<h3>Lista de Produtos</h3>';
            const lista = data.data;

            lista.forEach(produto => {
                produtosListDiv.innerHTML += `
                    <p>ID: ${produto.id}</p>
                    <p>Nome: ${produto.nome}</p>
                    <p>Preço: ${produto.preco}</p>
                    <p>Quantidade: ${produto.quantidade}</p>
                    <button onclick="deletarProduto(${produto.id})">Deletar</button>
                    <hr>
                `;
            });

            if (exibirMensagem) {
                alert("Produtos listados com sucesso!");
            }
        })
        .catch(error => {
            console.error('Erro ao listar produtos:', error);
        });
}

// Função para deletar um produto
function deletarProduto(id) {
    if (!confirm(`Tem certeza que deseja deletar o produto com ID ${id}?`)) {
        return;
    }

    fetch(`http://localhost:4567/produto/delete/${id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            alert(`Produto com ID ${id} deletado com sucesso!`);
            listarProdutos(false); // Atualiza a lista sem exibir a mensagem de sucesso
        } else {
            throw new Error(`Erro ao deletar produto. Status: ${response.status}`);
        }
    })
    .catch(error => {
        console.error('Erro ao deletar produto:', error);
        alert('Erro ao deletar produto. Verifique o console.');
    });
}
