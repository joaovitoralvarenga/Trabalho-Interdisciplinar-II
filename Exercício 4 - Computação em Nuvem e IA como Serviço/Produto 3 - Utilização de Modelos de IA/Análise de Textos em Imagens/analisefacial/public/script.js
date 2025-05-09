document.addEventListener('DOMContentLoaded', function() {
  const formulario = document.getElementById('formulario');
  const resultado = document.getElementById('resultado');
  const jsonResultado = document.getElementById('jsonResultado');
  const preview = document.getElementById('preview');
  const inputImagem = document.getElementById('imagem');
  const loading = document.getElementById('loading');

  // Detecta se estamos usando um servidor de desenvolvimento (como Live Server)
  const isDev = window.location.port === '5500';
  const serverUrl = isDev ? 'http://localhost:4567' : '';
  
  console.log('Usando URL do servidor:', serverUrl);

  // Verifica se o servidor está online - sem interromper o fluxo se falhar
  try {
      fetch(`${serverUrl}/status`)
          .then(response => {
              if (!response.ok) {
                  throw new Error(`Servidor não está respondendo corretamente (${response.status})`);
              }
              return response.json();
          })
          .then(data => {
              console.log('Status do servidor:', data.status);
          })
          .catch(error => {
              console.warn('Aviso: Não foi possível verificar status do servidor:', error);
              // O aplicativo continuará funcionando mesmo se o status check falhar
          });
  } catch (e) {
      console.warn('Erro ao tentar verificar status do servidor:', e);
  }

  // Preview da imagem antes do upload
  inputImagem.addEventListener('change', function() {
      const file = this.files[0];
      if (file) {
          const reader = new FileReader();
          reader.onload = function(e) {
              preview.src = e.target.result;
          }
          reader.readAsDataURL(file);
      }
  });

  // Envio do formulário
  formulario.addEventListener('submit', function(e) {
      e.preventDefault();
      
      const formData = new FormData(this);
      const file = inputImagem.files[0];
      
      if (!file) {
          alert('Por favor, selecione uma imagem para análise.');
          return;
      }
      
      // Verifica se é uma imagem
      if (!file.type.startsWith('image/')) {
          alert('Por favor, selecione um arquivo de imagem válido.');
          return;
      }
      
      // Oculta o resultado anterior e mostra o loading
      resultado.hidden = true;
      loading.style.display = 'block';
      
      // Adiciona um log para depuração
      console.log('Enviando imagem para análise...');
      
      // Envia a requisição para o servidor Java (porta 4567)
      fetch(`${serverUrl}/upload`, {
          method: 'POST',
          body: formData,
          // Não enviamos cabeçalhos Content-Type pois o browser cuida disso com o FormData
      })
      .then(response => {
          console.log('Resposta recebida:', response.status);
          
          if (!response.ok) {
              return response.text().then(text => {
                  console.error('Texto da resposta de erro:', text);
                  try {
                      const errorData = JSON.parse(text);
                      throw new Error(errorData.erro || `Erro do servidor: ${response.status}`);
                  } catch (jsonError) {
                      throw new Error(`Erro do servidor (${response.status}): ${text || 'Sem detalhes'}`);
                  }
              });
          }
          
          return response.text().then(text => {
              console.log('Resposta do servidor:', text.substring(0, 100) + '...');
              try {
                  return JSON.parse(text);
              } catch (e) {
                  console.error('Erro ao parsear JSON:', e);
                  throw new Error(`Resposta inválida do servidor: ${text.substring(0, 100)}...`);
              }
          });
      })
      .then(data => {
          // Primeiro, vamos mostrar o JSON bruto para depuração
          const jsonDiv = document.createElement('div');
          jsonDiv.innerHTML = `<h3>Dados brutos recebidos da API:</h3>
                              <pre style="background: #f5f5f5; padding: 10px; overflow: auto; max-height: 200px; font-size: 12px;">
                              ${JSON.stringify(data, null, 2)}
                              </pre>`;
          
          // Verifica se temos uma mensagem personalizada
          if (data.mensagem) {
              jsonResultado.innerHTML = `<p>${data.mensagem}</p>`;
              jsonResultado.appendChild(jsonDiv);
              resultado.hidden = false;
              loading.style.display = 'none';
              return;
          }
          
          // Formata o JSON para melhor visualização
          if (!Array.isArray(data) || data.length === 0) {
              jsonResultado.innerHTML = "<p>Nenhuma face detectada na imagem.</p>";
              jsonResultado.appendChild(jsonDiv);
          } else {
              // Exibe informações resumidas para cada face detectada
              let html = "<ul>";
              data.forEach((face, index) => {
                  html += `<li><strong>Face ${index + 1}:</strong><ul>`;
                  
                  // Informações de posição
                  if (face.faceRectangle) {
                      const rect = face.faceRectangle;
                      html += `<li>Posição: x=${rect.left}, y=${rect.top}, largura=${rect.width}, altura=${rect.height}</li>`;
                  }
                  
                  // Se houver ID do rosto
                  if (face.faceId) {
                      html += `<li>ID do rosto: ${face.faceId}</li>`;
                  }
                  
                  // Se houver mensagem de informação
                  if (face.info) {
                      html += `<li><em>${face.info}</em></li>`;
                  }
                  
                  // Informações de atributos (se disponíveis)
                  if (face.faceAttributes) {
                      const attrs = face.faceAttributes;
                      html += `<li>Atributos disponíveis: ${Object.keys(attrs).join(', ')}</li>`;
                      
                      // Processamento específico para atributos conhecidos
                      if (attrs.headPose) {
                          html += `<li>Orientação da cabeça: 
                              pitch=${attrs.headPose.pitch.toFixed(1)}°, 
                              roll=${attrs.headPose.roll.toFixed(1)}°, 
                              yaw=${attrs.headPose.yaw.toFixed(1)}°</li>`;
                      }
                      
                      if (attrs.glasses) {
                          html += `<li>Óculos: ${attrs.glasses === 'NoGlasses' ? 'Sem óculos' : 
                                  attrs.glasses === 'ReadingGlasses' ? 'Óculos de leitura' : 
                                  attrs.glasses === 'Sunglasses' ? 'Óculos de sol' : 
                                  attrs.glasses === 'SwimmingGoggles' ? 'Óculos de natação' : 
                                  attrs.glasses}</li>`;
                      }
                  }
                  
                  html += "</ul></li>";
              });
              html += "</ul>";
              
              // Adicionar uma mensagem sobre as limitações da API
              html += `<div class="alert alert-info">
                         <p><strong>Nota:</strong> A Microsoft descontinuou vários recursos de análise facial por questões éticas, 
                         incluindo detecção de idade, gênero, emoção, cabelo e barba. 
                         <a href="https://aka.ms/facerecognition" target="_blank">Saiba mais</a>.</p>
                       </div>`;
              
              jsonResultado.innerHTML = html;
              jsonResultado.appendChild(jsonDiv);
          }
          
          // Mostra a seção de resultado e esconde o loading
          resultado.hidden = false;
          loading.style.display = 'none';
      })
      .catch(error => {
          // Trata erros
          loading.style.display = 'none';
          resultado.hidden = false;
          jsonResultado.innerHTML = `<div class="error">Erro: ${error.message}</div>`;
          console.error('Erro ao analisar imagem:', error);
      });
  });
});