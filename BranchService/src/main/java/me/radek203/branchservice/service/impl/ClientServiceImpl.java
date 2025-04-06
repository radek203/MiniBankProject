package me.radek203.branchservice.service.impl;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.config.AppProperties;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.entity.ClientStatus;
import me.radek203.branchservice.repository.ClientRepository;
import me.radek203.branchservice.service.ClientService;
import me.radek203.branchservice.service.KafkaSenderService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@AllArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final KafkaSenderService kafkaSenderService;
    private final ClientRepository clientRepository;
    private final AppProperties appProperties;

    /**
     * We do not check if generated account number exists in other branches, We can do it but, for our example
     * we want to show transaction compensation.
     * The best way to do this is to use e.g. prefix or suffix for every headquarter and branch.
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
}
