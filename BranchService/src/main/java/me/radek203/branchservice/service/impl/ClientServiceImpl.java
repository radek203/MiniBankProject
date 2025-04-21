package me.radek203.branchservice.service.impl;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.client.CreditCardClient;
import me.radek203.branchservice.config.AppProperties;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.entity.ClientStatus;
import me.radek203.branchservice.entity.CreditCard;
import me.radek203.branchservice.exception.ResourceNotFoundException;
import me.radek203.branchservice.repository.ClientRepository;
import me.radek203.branchservice.service.ClientService;
import me.radek203.branchservice.service.KafkaSenderService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final KafkaSenderService kafkaSenderService;
    private final ClientRepository clientRepository;
    private final AppProperties appProperties;
    private final CreditCardClient creditCardClient;

    /**
     * We do not check if the generated account number exists in other branches, We can do it but, for our example,
     * we want to show transaction compensation.
     * The best way to do this is to use e.g., prefix or suffix for every headquarters and branch.
     *
     * @return random account number
     */
    private String getRandomAccountNumber() {
        SecureRandom random = new SecureRandom();
        List<String> numbers = clientRepository.getAllAccountNumbers();
        String number;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                sb.append(random.nextInt(10));
            }
            number = sb.toString();
        } while (numbers.contains(number));
        return number;
    }

    @Override
    public Client saveClient(Client client) {
        client.setBranch(appProperties.getBranchId());
        client.setStatus(ClientStatus.CREATING);
        client.setBalance(0);
        client.setBalanceReserved(0);
        client.setAccountNumber(getRandomAccountNumber());
        Client clientSaved = clientRepository.save(client);
        kafkaSenderService.sendMessage("headquarter-client-create", String.valueOf(clientSaved.getId()), clientSaved);
        return clientSaved;
    }

    @Override
    public Client updateClient(UUID id, Client client) {
        Client clientFound = clientRepository.findByIdAndUserId(id, client.getUserId()).orElseThrow(() -> new ResourceNotFoundException("error/account-not-found", String.valueOf(id)));
        clientFound.setAddress(client.getAddress());
        clientFound.setCity(client.getCity());
        clientFound.setFirstName(client.getFirstName());
        clientFound.setLastName(client.getLastName());
        clientFound.setPhone(client.getPhone());
        return clientRepository.save(clientFound);
    }

    @Override
    public Client getClientById(UUID id, int userId) {
        return clientRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new ResourceNotFoundException("error/account-not-found", String.valueOf(id)));
    }

    @Override
    public CreditCard orderCreditCard(String accountNumber, int userId) {
        clientRepository.findByAccountNumberAndUserId(accountNumber, userId).orElseThrow(() -> new ResourceNotFoundException("error/account-not-found", String.valueOf(accountNumber)));
        return creditCardClient.getResponse("error/credit-card-creation", creditCardClient::createCreditCard, appProperties.getBranchId(), accountNumber);
    }

    @Override
    public void deleteCreditCard(String number, int userId) {
        creditCardClient.getResponse("error/credit-card-deletion", creditCardClient::deleteCreditCard, number, userId);
    }

    @Override
    public void completedClient(UUID id) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        if (clientOptional.isEmpty() || clientOptional.get().getStatus() == ClientStatus.ACTIVE) {
            return;
        }
        Client client = clientOptional.get();
        client.setStatus(ClientStatus.ACTIVE);
        clientRepository.save(client);
    }

    @Override
    public void failedClient(UUID id) {
        clientRepository.deleteById(id);
    }
}
