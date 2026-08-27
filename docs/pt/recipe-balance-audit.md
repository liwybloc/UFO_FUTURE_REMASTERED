# Auditoria de Balanceamento de Receitas

Este documento audita o conjunto atual de receitas do UFO Future e propõe buffs, nerfs e refatorações para uma progressão standalone mais difícil em `UFO Future + AE2`.

A auditoria foi baseada no estado real do repositório, tratando receitas geradas e código como fonte de verdade.

## Escopo

Arquivos de receita auditados:

- `42` receitas de crafting geradas
- `103` receitas DMA geradas
- `11` simulações do Stellar Nexus
- `14` receitas handcrafted de multibloco universal
- `2` receitas universais geradas
- `10` receitas de desmontagem

Total auditado: `182` arquivos de receita

Auditoria de cobertura:

- `148` itens e block-items `ufo:` registrados verificados
- `53` IDs registrados atualmente não possuem saída de recipe nos dados auditados

## Resumo Executivo

O mod já tem uma base muito boa de late-game:

- a progressão do DMA é real e coerente
- a cadeia de fragmentos -> matter -> matrices é sólida
- os multiblocos universais já suportam throughput industrial de processadores
- o Stellar Nexus já opera em escala de bilhões de AE e milhões de itens

Os maiores problemas não são "falta de endgame". Eles são:

1. A cobertura de receitas está incompleta para vários blocos e ferramentas visíveis ao jogador.
2. Algumas famílias de receita ainda estão planas demais para o poder que entregam.
3. Certos itens endgame têm tema forte, mas ainda pouco peso de cascata.
4. O custo das Infinity Cells é uniforme demais entre alvos triviais e absurdos.
5. O próprio multibloco do Stellar Nexus precisa de gating estrutural mais pesado se ele for o verdadeiro endgame.

## Descobertas Confirmadas no Código

Fatos importantes confirmados durante a auditoria:

- o Safe Mode do Stellar Nexus multiplica energia, combustível e coolant por `2.5x`
- o overclock do Stellar Nexus multiplica energia inicial por `10x`, combustível e coolant por `5x`, calor por `5x` e progresso por `5x`
- a capacidade global de energia do Stellar Nexus é `20.000.000.000 AE`
- o buffer interno base do DMA é `500.000 AE`
- o empilhamento de catalisadores do DMA já tem tradeoffs térmicos fortes e sinergia de 4 slots

As docs estão um pouco atrasadas em relação ao código em algumas áreas, principalmente na escala de custo do Stellar Nexus.

## Auditoria de Cobertura

### Receitas Provavelmente Faltando Para Itens Voltados ao Jogador

Os IDs abaixo aparecem registrados, mas não aparecem como outputs nas receitas auditadas:

- `ufo:ae_energy_input_hatch`
- `ufo:me_massive_fluid_hatch`
- `ufo:me_massive_input_hatch`
- `ufo:me_massive_output_hatch`
- `ufo:entropy_assembler_core_casing`
- `ufo:entropy_computer_condensation_matrix`
- `ufo:entropy_singularity_casing`
- `ufo:stellar_field_generator_t1`
- `ufo:stellar_field_generator_t2`
- `ufo:stellar_field_generator_t3`
- `ufo:stellar_nexus_controller`
- `ufo:quantum_entropy_casing`
- `ufo:structure_scanner`
- `ufo:ufo_sword`
- `ufo:ufo_pickaxe`
- `ufo:ufo_axe`
- `ufo:ufo_shovel`
- `ufo:ufo_hoe`
- `ufo:ufo_hammer`
- `ufo:ufo_greatsword`
- `ufo:ufo_bow`
- `ufo:ufo_fishing_rod`
- `ufo:bismuth`

Recomendação:

- adicionar receitas reais para todas as partes estruturais e hatches do Stellar Nexus
- ou adicionar receitas para as ferramentas de energia, ou convertê-las em transformações ou upgrades a partir da `ufo:ufo_staff`
- ou implementar uma fonte de gameplay para `bismuth`, ou removê-lo da progressão do jogador

### IDs Provavelmente Intencionais Sem Crafting Direto

Estes podem ficar sem receita direta se a expectativa for obtê-los via manipulação de fluido:

- fluid blocks
- buckets

Exemplos:

- `ufo:liquid_starlight_bucket`
- `ufo:temporal_fluid_bucket`
- `ufo:uu_matter_bucket`
- `ufo:raw_star_matter_plasma_fluid_block`

### Lacuna Condicional de Integração

Estes itens estão registrados, mas não possuem receitas correspondentes auditadas:

## Auditoria por Família

## 1. Receitas de Crafting Padrão

### O que existe

Esta camada contém outputs diretos de crafting do mod, como:

- `obsidian_matrix`
- `graviton_plated_casing`
- `dimensional_matter_assembler`
- blocos e nuggets de fragmento
- controladores de multiblocos quânticos
- crafting storages grandes
- mega co-processors

### Veredito

- `obsidian_matrix` é uma âncora boa e deve continuar simples
- `graviton_plated_casing` e `dimensional_matter_assembler` são bons gates iniciais
- mega storages e mega co-processors estão planos demais para o que representam
- os controladores quânticos têm tema forte, mas ainda pouco sink profundo de processadores

### Buff / Nerf / Refatoração

#### Manter

- `obsidian_matrix`
- `graviton_plated_casing`
- `dimensional_matter_assembler`

#### Nerfar por Refatoração

Os itens abaixo devem virar cadeias de upgrade mais profundas em vez de conversões quase de um salto:

- mega crafting storages
- mega co-processors

Problema atual:

- muitos upgrades de storage e co-processor são basicamente "tier anterior + 1 item de prestígio"
- isso passa sensação de compressão, não de construção endgame

Refatoração sugerida:

- exigir múltiplas cópias do tier anterior, não uma só
- adicionar `dimensional_processor` ou futuras famílias avançadas de processador como sinks recorrentes
- amarrar os storages de topo às famílias de matrix
- amarrar os co-processors de topo a cadeias pesadas de engineering e processors

Direção desejada:

- `mk2 ~= 4x a 8x` do item anterior com processadores de suporte
- `mk3 ~= 8x a 16x` do item anterior com gates de matter ou matrix

#### Buffar

Os controladores de multibloco quântico deveriam definir mais a fábrica.

Sugestão:

- manter os temas atuais de item
- adicionar contagens de famílias de processador e dependências de multibloco anterior
- deixar `quantum_processor_assembler_controller` e `quantum_matter_fabricator_controller` claramente mais caros que `quantum_slicer_controller`

## 2. Receitas Bootstrap do DMA

### O que existe

A camada de bootstrap do DMA inclui:

- `dust_blizz_bootstrap`
- `cryotheum_dust_bootstrap`
- `gelid_cryotheum_bootstrap`
- a prensa do processador dimensional e a primeira linha do dimensional processor

### Veredito

Esta seção está saudável. Ela suporta progressão AE2-only sem hard lock imediato em mods extras.

### Buff / Nerf / Refatoração

- manter as receitas bootstrap
- manter o perfil de custo baixo
- preservar a sensação de "inputs simples, outputs úteis"

Única sugestão menor:

- unificar naming e linguagem nas docs para o jogador entender quais receitas são starters de fallback e quais já são rotas industriais permanentes

## 3. Cadeia DMA de Fragmentos Estelares

### O que existe

A progressão de lingotes atualmente segue:

- `white_dwarf_fragment_ingot`
- `neutron_star_fragment_ingot`
- `pulsar_fragment_ingot`

Depois disso vêm os derivados:

- dust
- fluid
- rods
- blocks

### Veredito

Esta é uma das famílias de receita mais fortes do mod. A identidade é clara e a escalada funciona bem.

### Buff / Nerf / Refatoração

#### Manter

- a escalada linear atual
- liquid starlight como backbone comum de fluidos

#### Buff leve

`pulsar_fragment` já é importante o suficiente para justificar um gate um pouco mais pesado.

Mudança sugerida:

- pequeno aumento de AE
- adicionar mais um ingrediente avançado ou subir os counts atuais

Motivo:

- derivados de pulsar alimentam anomaly, matter e progressão high-end

## 4. Economia de Fluidos e Matter do DMA

### O que existe

Esta família inclui:

- `uu_amplifier`
- `uu_matter`
- `gelid_cryotheum`
- `stable_coolant`
- `liquid_starlight`
- `raw_star_matter_plasma`
- `transcending_matter_fluid`
- `primordial_matter_liquid`
- fluidos `spatial` e `temporal`
- `neutronium_sphere`
- `enriched_neutronium_sphere`
- `proto_matter`
- `corporeal_matter`
- matters estelares
- `dark_matter`

### Veredito

Este é o verdadeiro coração da progressão do UFO Future. Funciona, mas algumas receitas ainda estão baratas demais em relação ao quanto o restante do endgame depende delas.

### Buff / Nerf / Refatoração

#### Buffar

`raw_star_matter_plasma`

Motivo:

- é o fluido backbone do Stellar Nexus
- hoje ele parece mais um fluido de produção do que um precursor em escala civilizatória

Mudança sugerida:

- aumentar energia e tempo
- adicionar um input high-tier extra, possivelmente `dimensional_processor` ou `corporeal_matter`

#### Buffar

`enriched_neutronium_sphere`

Motivo:

- ele gateia uma quantidade enorme de output endgame
- o custo atual é bom, mas ainda não é assustador o suficiente para as fábricas mk3 que você quer

Mudança sugerida:

- aumentar volume de fluidos
- adicionar requisito de processador

#### Buffar

`dark_matter`

Motivo:

- este é um material de prestígio e deveria ser memorável
- a receita atual é boa, mas ainda é majoritariamente compressão de contagem bruta

Refatoração sugerida:

- manter os requisitos atuais de stellar matter
- adicionar pressão de component ou processor
- opcionalmente exigir um catalyst tardio ou uma matrix high-end

#### Manter

- `proto_matter`
- `corporeal_matter`
- as receitas tieradas de stellar matter

Essas já comunicam progressão muito bem.

## 5. Família DMA de Catalisadores

### O que existe

O mod já tem quatro famílias completas de catalisador:

- Matterflow
- Chrono
- Overflux
- Quantum

Cada uma com T1, T2 e T3.

### Veredito

A identidade das famílias é excelente. O que deve bater mais forte são as receitas, especialmente T3.

### Buff / Nerf / Refatoração

#### Buffar

Todos os catalisadores T3

Motivo:

- catalisadores T3 definem o endgame
- o impacto deles no gameplay já é forte no código
- o custo de recipe deveria acompanhar esse poder com mais agressividade

Refatoração sugerida:

- exigir mais de um catalisador do tier anterior
- adicionar inputs de processor ou matrix
- diferenciar a identidade da família por matter e fluido

#### Nerfar

A acessibilidade relativa do Overflux T3

Motivo:

- calor negativo ou neutro é um dos utilitários mais fortes do sistema inteiro
- a receita não deveria cair perto demais das outras se ela estabiliza builds perigosas

Mudança sugerida:

- subir levemente AE, tempo ou count de input de prestígio

#### Manter

- os temas atuais de família
- a separação de identidade por fluido

## 6. Cadeia de Component Matrices do DMA

### O que existe

A cadeia atual de matrices é:

- `phase_shift_component_matrix`
- `hyper_dense_component_matrix`
- `tesseract_component_matrix`
- `event_horizon_component_matrix`
- `cosmic_string_component_matrix`

### Veredito

Esta cadeia é excelente estruturalmente, mas a pressão de quantidade ainda está leve demais para um endgame estilo GTNH.

### Buff / Nerf / Refatoração

#### Buffar por Refatoração

Faça cada matrix consumir mais da matrix anterior e mais processadores.

Direção sugerida:

- `phase_shift`: manter acessível
- `hyper_dense`: aumentar demanda de dimensional processor e componentes AE
- `tesseract`: exigir mais matrices anteriores
- `event_horizon`: introduzir pressão séria de matter
- `cosmic_string`: virar um bottleneck real, não só um craft de prestígio

Sensação desejada:

- cosmic string deve ser o item em torno do qual o jogador monta fábricas

## 7. Infinity Cells

### O que existe

Existem `29` receitas geradas de Infinity Cell, todas com a mesma estrutura:

- `cosmic_string_component_matrix`
- `quantum_anomaly`
- `64` do item alvo
- `250.000.000 AE`
- `10.000 ticks`

### Veredito

Isso é elegante, mas uniforme demais. Uma infinity cell de cobblestone e uma de antimatter não deveriam custar a mesma coisa.

### Buff / Nerf / Refatoração

#### Refatorar Forte

Separar as Infinity Cells em faixas de custo:

- materiais comuns de bulk
  - cobblestone, sand, water, gravel, logs
- materiais avançados de utilidade
  - obsidian, amethyst, end stone, lava, sky stone

Regra sugerida:

- manter o padrão base
- variar AE, tempo, volume de fluido e tier de matrix exigido

Direção de exemplo:

- common cells: custo atual ou levemente menor
- advanced cells: baseline atual
- exotic cells: significativamente mais caras, talvez com `dark_matter` ou segunda matrix

## 8. Receitas de Multibloco Universal

### O que existe

As camadas atuais de multibloco universal são:

- Quantum Slicer: massificação de processadores impressos
- Quantum Processor Assembler: massificação de processadores finalizados
- QMF: polish de stable coolant
- receita gerada do dimensional processor no QPA

### Veredito

Esta é uma das melhores partes do mod. Ela resolve um problema real de escala e abre espaço para craftings absurdos depois.

### Buff / Nerf / Refatoração

#### Manter

- a produção massiva de processadores AE2
- a ideia de um bloco de input virar um lote grande de processadores

#### Buff leve

Linhas avançadas deveriam destravar depois das linhas básicas de AE2.

Mudança sugerida:

- manter printed logic, calculation e engineering em tier 1
- mover dimensional e processadores avançados cross-mod para tier maior do multibloco ou custo maior da máquina

#### Refatorar

Usar essas máquinas como backbone dos futuros controladores mk2 e mk3.

Ou seja:

- controladores endgame deveriam consumir outputs do Quantum Slicer e do Quantum Processor Assembler em quantidades absurdas

## 9. Receitas de Simulação do Stellar Nexus

### O que existe

O conjunto atual já cobre:

- simulações baratas de integração
- simulações de material avançado em custo intermediário
- sínteses massivas reais em escala de bilhões de AE

Exemplos atuais de simulação mk3:

- `Massive Iron Synthesis`: `2B AE`, `45 min`, `15M iron`
- `Massive Copper Synthesis`: `2B AE`, `45 min`, `15M copper`
- `Massive Gold Synthesis`: `2.5B AE`, `50 min`, `15M gold`
- `Massive Netherite Synthesis`: `5B AE`, `80 min`, `5M netherite`

### Veredito

A direção está correta. A máquina já faz o que uma máquina endgame deveria fazer. O ponto fraco é o gating estrutural, não a filosofia de recompensa.

### Buff / Nerf / Refatoração

#### Manter

- outputs em escala de milhões
- custos iniciais em bilhões de AE
- runtimes longos
- alta demanda de fluidos precursores

#### Buffar

O custo de construção do multibloco em si

Motivo:

- se este é o payoff industrial final, as partes da estrutura precisam parecer um megaprojeto

Nova regra sugerida:

- máquina Mk1 = primeiro grande breakthrough industrial
- máquina Mk2 = cerca de `10x` o custo total da Mk1
- máquina Mk3 = cerca de `50x` o custo total da Mk2

#### Refatorar

Separar a máquina em construção explicitamente tierada:

- `field_generator_mk1`
- `field_generator_mk2`
- `field_generator_mk3`
- upgrades de controller ou controller cores tierados

Depois escalar:

- peças da máquina anterior
- counts de processador
- counts de matrix
- counts de matter
- contagem de casing

#### Buffar

As simulações de integração "soft", como alguns outputs modded de custo muito baixo

Motivo:

- algumas parecem mais showcase recipes do que programas verdadeiros de Stellar Nexus

Correção sugerida:

- ou reposicionar como simulações de entrada ou tutorial
- ou torná-las materialmente mais caras para pertencerem à mesma fantasia da máquina

## 10. Receitas de Desmontagem

### O que existe

Existem `10` receitas de desmontagem de storage cells do AE2 para células white dwarf e neutron star.

### Veredito

Isso é um bom QoL e deve permanecer.

### Buff / Nerf / Refatoração

- manter como está
- não precisa de rebalance relevante agora

Se você tornar storage cells dramaticamente mais caras no futuro, mantenha a desmontagem generosa o bastante para a experimentação não ficar punitiva.

## Ordem de Prioridade Recomendada

Se você quiser o rebalance de maior impacto primeiro, faça nesta ordem:

1. Adicionar as receitas faltantes das partes do Stellar Nexus, hatches, field generators, controller e scanner.
2. Refatorar mega storages e mega co-processors em upgrades de cascata mais profundos.
3. Refatorar o custo das Infinity Cells em faixas common, advanced e exotic.
4. Buffar catalisadores T3 e recipes high-tier de matrix.
5. Buffar `raw_star_matter_plasma`, `enriched_neutronium_sphere` e `dark_matter`.
6. Transformar outputs do Quantum Slicer e do Quantum Processor Assembler em sinks enormes para futuros multiblocos mk2 e mk3.
7. Introduzir custos reais de construção Mk1, Mk2 e Mk3 no Stellar Nexus usando sua regra de `10x` e depois `50x`.

## Direção de Design Concreta Para a Sua Visão

Para a fantasia exata que você descreveu, o melhor caminho é:

- manter os outputs atuais do Stellar Nexus em escala de milhões
- subir muito mais o custo de construção da máquina do que o custo dos outputs
- empurrar a absurdidade para cascatas de processadores e peças tieradas de máquina
- transformar `cosmic_string`, `dark_matter`, `charged_enriched_neutronium_sphere` e linhas avançadas de processador nos bottlenecks de assinatura

Isso entrega um late-game duro na subida, mas realmente recompensador quando o jogador conclui a infraestrutura.
