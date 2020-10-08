package pl.coderslab.charityApp.institution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionsServiceImpl implements InstitutionService {
    private final InstitutionRepository institutionsRepository;

    @Override
    public List<Institution> findAll() {
        return institutionsRepository.findAll();
    }
}
